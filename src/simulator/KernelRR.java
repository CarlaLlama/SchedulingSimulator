/*
 * Kernel implementation for Round Robin simulation
 * 
 * @author Carla Wilby
 * @version 14/04/2016
 */
package simulator;

public class KernelRR implements Kernel{
    
  @Override
    public int syscall(int number, Object... varargs) {
        switch(number){
            case(MAKE_DEVICE):
                
                break;
            case(EXECVE):
                break;
            case(IO_REQUEST):
                break;
            case(TERMINATE_PROCESS):
                break;
        }
        return 0;
    }

    @Override
    public void interrupt(int interruptType, Object... varargs) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
