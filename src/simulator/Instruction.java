package simulator; 
/**
 * Representation of program instruction in abstract.
 * 
 * @author Stephan Jamieson
 * @version 12/3/2016
 */
public abstract class Instruction {

    private int duration;
    
    /**
     * Create an instruction of the given duration for the given process.
     */
    Instruction(int duration) {
        this.duration = duration;
    }
        
    /**
     * Obtain this instruction's duration.
     */
    public int getDuration() { return duration; }
    
}


