/*
 * ProcessControlBlock implementation
 * 
 * @author Carla Wilby
 * @version 14/04/2016
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import simulator.CPUInstruction;
import simulator.IOInstruction;
import simulator.Instruction;
import simulator.ProcessControlBlock;
import simulator.ProcessControlBlock.State;

public class ProcessControlBlockImpl implements ProcessControlBlock{
    
    private int PID;
    private static int pc = 1;
    private String programName;
    private int priority;
    private State state;
    private List<Instruction> instructionList;
    
    public ProcessControlBlockImpl(){
        instructionList = new ArrayList<>();
        PID = pc++;
    }

    @Override
    public int getPID() {
        return PID;
    }

    @Override
    public String getProgramName() {
        return programName;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public int setPriority(int value) {
        int temp = priority;
        priority = value;
        return temp;
    }
    
    @Override
    public Instruction getInstruction() {
        return instructionList.get(0);
    }

    @Override
    public boolean hasNextInstruction() {
       return (instructionList.size()>1);
    }

    @Override
    public void nextInstruction() {
        instructionList.remove(0);
    }
    
    public void setInstructionList(List<Instruction> il) {
        instructionList = il;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }
    
    @Override
    public String toString(){
        return String.format("process(pid=%d, state=%s, name=\"%s\")", this.getPID(), this.getState(), this.getProgramName());
    }
    
    public static ProcessControlBlock loadProgram(String filename) {
        ProcessControlBlockImpl pcb = new ProcessControlBlockImpl();
        try {
            final BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            while (line!=null) {
                // PRINT LINE
                line.trim();
                if (line.startsWith("#") || line.equals("")) {
                    // It's a commment or blank line, ignore.
                }
                else if (line.startsWith("CPU")) {
                    Scanner scanner = new Scanner(line);
                    scanner.next(); 
                    if (!scanner.hasNextInt()) {
                        System.out.println("CPU burst time missing: \""+line+"\".");
                        System.exit(-1);
                    }
                    final int cpuBurstTime = scanner.nextInt();
                    CPUInstruction cpuInst = new CPUInstruction(cpuBurstTime);
                    pcb.instructionList.add(cpuInst);
                }
                else if (line.startsWith("IO")) {
                    Scanner scanner = new Scanner(line);
                    scanner.next();
                    if (!scanner.hasNextInt()) {
                        System.out.println("IO burst time missing: \""+line+"\".");
                        System.exit(-1);
                    }
                    
                    final int ioBurstTime = scanner.nextInt();
                    if (!scanner.hasNextInt()) {
                        System.out.println("IO missing device type: \""+line+"\".");
                        System.exit(-1);
                    }
                    final int ioID = scanner.nextInt();
                    IOInstruction ioInst = new IOInstruction(ioBurstTime,ioID);
                    pcb.instructionList.add(ioInst);
                }
                else {
                    System.out.println("Unrecognised token in configuration file : \""+filename+"\".");
                    reader.close();
                    System.exit(-1);
                }
                line = reader.readLine();
            }
            reader.close();
            pcb.state = State.READY;
            pcb.programName = filename;
            return pcb;
        }
        catch (FileNotFoundException e) {
            System.out.println("File \""+filename+"\" not found.");
            System.exit(-1);
        }
        catch (IOException i) {
            System.out.println("IO Error reading from \""+filename+"\".");
            System.exit(-1);
        }  
        return pcb;
    }
}
