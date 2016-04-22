/*
 * Driver file for FCFS simulation
 * 
 * @author Carla Wilby
 * @version 14/04/2016
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import simulator.Config;
import simulator.Config;
import simulator.SystemTimer;
import simulator.SystemTimer;
import simulator.TRACE;
import simulator.TRACE;

public class SimulateFCFS {
    
    public static void main(String[] args){
        BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
        String filename;
        int costsys;
        int costcont;
        int trace;
        
        System.out.println("Working Directory = " +
              System.getProperty("user.dir"));
        
        try{
            System.out.println("*** FCFS Simulator ***");
            System.out.print("Enter configuration file name:");
            filename = b.readLine();
            System.out.print("Enter cost of system call: ");
            costsys = Integer.parseInt(b.readLine());
            System.out.print("Enter cost of context switch: ");
            costcont = Integer.parseInt(b.readLine());
            System.out.print("Enter trace level: ");
            trace = Integer.parseInt(b.readLine());
            int t = TRACE.SET_TRACE_LEVEL(trace);
            final KernelFCFS kernel = new KernelFCFS();

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
