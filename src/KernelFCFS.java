/*
 * Kernel implementation for FCFS simulation
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

public class KernelFCFS implements Kernel {
    

    private final Deque<ProcessControlBlock> readyQueue;
        
    public KernelFCFS() {
	readyQueue = new ArrayDeque<>();
    }
    
    private ProcessControlBlock dispatch() {
        if(!readyQueue.isEmpty()){
            ProcessControlBlock pcb = readyQueue.pop();
            ProcessControlBlock oldpcb = Config.getCPU().contextSwitch(pcb);
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
                    this.dispatch();
                }
                break;
             case TERMINATE_PROCESS:
                {
		    ProcessControlBlock current = Config.getCPU().getCurrentProcess();
                    current.setState(ProcessControlBlock.State.TERMINATED);
                    this.dispatch();
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
                throw new IllegalArgumentException("FCFSKernel:interrupt("+interruptType+"...): this kernel does not support timeouts.");
            case WAKE_UP:
                ProcessControlBlock current = (ProcessControlBlock)varargs[1];
                current.setState(ProcessControlBlock.State.READY);
                readyQueue.add(current);
		if(Config.getCPU().isIdle()){
                    this.dispatch();
                }
                break;
            default:
                throw new IllegalArgumentException("FCFSKernel:interrupt("+interruptType+"...): unknown type.");
        }
    }
    
    private static ProcessControlBlock loadProgram(String filename) {
        return ProcessControlBlockImpl.loadProgram(filename);
    }
}
