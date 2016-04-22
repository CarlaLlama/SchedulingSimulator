/*
 * Driver file for Round Robin simulation
 * 
 * @author Carla Wilby
 * @version 14/04/2016
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import simulator.Config;
import simulator.SystemTimer;
import simulator.TRACE;

public class SimulateRR {
    
    public static void main(String[] args){
        BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
        String filename;
        int costsys;
        int costcont;
        int trace;
        int slice;
        
        try{
            System.out.println("*** RR Simulator ***");
            System.out.print("Enter configuration file name:");
            filename = b.readLine();
            System.out.print("Enter slice time: ");
            slice = Integer.parseInt(b.readLine());
            System.out.print("Enter cost of system call: ");
            costsys = Integer.parseInt(b.readLine());
            System.out.print("Enter cost of context switch: ");
            costcont = Integer.parseInt(b.readLine());
            System.out.print("Enter trace level: ");
            trace = Integer.parseInt(b.readLine());
            int t = TRACE.SET_TRACE_LEVEL(trace);
            final KernelRR kernel = new KernelRR(slice);

            Config.init(kernel, costcont, costsys);

            Config.buildConfiguration(filename);
            Config.run();
            System.out.println("*** Results ***");
            SystemTimer timer = Config.getSystemTimer();
            System.out.println(timer);
            System.out.println("Context switches:" + Config.getCPU().getContextSwitches());
            System.out.printf("CPU utilization: %.2f\n", (double)timer.getUserTime()/timer.getSystemTime()*100);
        
        }catch(IOException e){
            System.out.println("Please enter the correct inputs.");
        }
        
    }
}
