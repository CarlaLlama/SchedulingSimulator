/*
 * Kernel implementation for RR simulation
 * 
 * @author Carla Wilby
 * @version 14/04/2016
 */

import simulator.Config;
import simulator.IODevice;
import simulator.Kernel;
import simulator.ProcessControlBlock;
//
import java.util.ArrayDeque;
import java.util.Deque;
import simulator.InterruptHandler;
import static simulator.InterruptHandler.TIME_OUT;
import static simulator.InterruptHandler.WAKE_UP;
import static simulator.SystemCall.EXECVE;
import static simulator.SystemCall.IO_REQUEST;
import static simulator.SystemCall.MAKE_DEVICE;
import static simulator.SystemCall.TERMINATE_PROCESS;

public class KernelRR implements Kernel {
    

    private final Deque<ProcessControlBlock> readyQueue;
    private static int TIMESLICE;
        
    public KernelRR(int ts) {
	readyQueue = new ArrayDeque<>();
        TIMESLICE = ts;
    }
    
    private ProcessControlBlock dispatch() {
        if(!readyQueue.isEmpty()){
            ProcessControlBlock pcb = readyQueue.peek();
            ProcessControlBlock oldpcb = Config.getCPU().contextSwitch(readyQueue.pop());
            Config.getSystemTimer().scheduleInterrupt(TIMESLICE, this, pcb.getPID());
            pcb.setState(ProcessControlBlock.State.RUNNING);
            return oldpcb;
        }else{
            return Config.getCPU().contextSwitch(null);
        }
}
                
    @Override
    public int syscall(int number, Object... varargs) {
        int result = 0;
        switch (number) {
             case MAKE_DEVICE:
                {
                    IODevice device = new IODevice((Integer)varargs[0], (String)varargs[1]);
                    Config.addDevice(device);
                }
                break;
             case EXECVE: 
                {
                    ProcessControlBlock pcb = loadProgram((String)varargs[0]);
                    if (pcb!=null) {
                        readyQueue.add(pcb);
			if(Config.getCPU().isIdle()){
                            this.dispatch();
                            pcb.setState(ProcessControlBlock.State.RUNNING);
                           // Config.getSystemTimer().scheduleInterrupt(TIMESLICE, this, pcb.getPID());
                        }
                    }else {
                        result = -1;
                    }
                    
                }
                break;
             case IO_REQUEST: 
                {
                    ProcessControlBlock current = Config.getCPU().getCurrentProcess();
                    IODevice currDev = Config.getDevice((Integer)varargs[0]);
                    currDev.requestIO((Integer)varargs[1], current, this);
                    current.setState(ProcessControlBlock.State.WAITING);
                    Config.getSystemTimer().cancelInterrupt(current.getPID());
                    this.dispatch();
                    //Config.getSystemTimer().scheduleInterrupt(TIMESLICE, this, Config.getCPU().getCurrentProcess().getPID());
                }
                break;
             case TERMINATE_PROCESS:
                {
		    ProcessControlBlock current = Config.getCPU().getCurrentProcess();
                    current.setState(ProcessControlBlock.State.TERMINATED);
                    Config.getSystemTimer().cancelInterrupt(current.getPID());
                    this.dispatch();
                    //Config.getSystemTimer().scheduleInterrupt(TIMESLICE, this, Config.getCPU().getCurrentProcess().getPID());
                }
                break;
             default:
                result = -1;
        }
        return result;
    }
   
    
    @Override
    public void interrupt(int interruptType, Object... varargs){
        switch (interruptType) {
            case TIME_OUT:
                ProcessControlBlock curr = Config.getCPU().getCurrentProcess();
                curr.setState(ProcessControlBlock.State.READY);
                readyQueue.add(curr);
                this.dispatch();
                //Config.getSystemTimer().scheduleInterrupt(TIMESLICE, this, Config.getCPU().getCurrentProcess().getPID());
                Config.getCPU().getCurrentProcess().setState(ProcessControlBlock.State.RUNNING);
                break;
            case WAKE_UP:
                ProcessControlBlock current = (ProcessControlBlock)varargs[1];
                current.setState(ProcessControlBlock.State.READY);
                readyQueue.add(current);
		if(Config.getCPU().isIdle()){
                    this.dispatch();
                }
                //Config.getSystemTimer().scheduleInterrupt(TIMESLICE, this, Config.getCPU().getCurrentProcess().getPID());
                break;
            default:
                throw new IllegalArgumentException("RRKernel:interrupt("+interruptType+"...): unknown type.");
        }
    }
    
    private static ProcessControlBlock loadProgram(String filename) {
        return ProcessControlBlockImpl.loadProgram(filename);
    }
}
