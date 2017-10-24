/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src.simple;

import Logger.SimpleLoggerLight11;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class RestarterSimpleAgent {
    
//    public static void UsageExample(){
//        try {
//            HelpM.run_application_jar_with_arguments("restarter.jar", "NetProcMonitor.jar", "1",".");
//            Thread.sleep(1500);
//            System.exit(0);
//        } catch (Exception ex) {
//            Logger.getLogger(HelpM2.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
    /**
     * This one fits nice to launch the restarter with
     * run_application_jar_with_arguments("restarter.jar", "NetProcMonitor.jar", "1",".");
     * @param application_to_run_name
     * @param arg1
     * @param arg2
     * @param path
     * @throws IOException 
     */
    public static void run_application_jar_with_arguments(String application_to_run_name, String arg1, String arg2, String path) throws IOException {
        String[] commands = new String[5];
        if (application_to_run_name.contains(".jar")) {
            commands[0] = "java";
            commands[1] = "-jar";
            commands[2] = application_to_run_name; //OBS! pay attention here
            commands[3] = arg1;
            commands[4] = arg2;
        }
        ProcessBuilder builder = new ProcessBuilder(commands);
        builder.directory(new File(path));
        builder.start();
    }

    public static void main(String[] args) {

        //Note!!!
        //You must have the second parameter, otherwise will not work
        //String[] commands2 = {"java", "-jar", "restarter.jar", "OPCRecorderParalel.jar","0"};
        if (args.length == 0) {
            System.exit(0);
        }

        if (args.length == 2) {
            //
            if (args[1] == null) {
                args[1] = "0"; // I set the arg to '0' because it doesnt take an empty arg!!!!! I spent a lot of time with this shit
            }
            //
            runJavaApp(args[0], args[1]);
            //
        }

        if (args.length == 1) {
            runJavaApp(args[0], "0");
        }


    }

    /**
     * Runs a java
     *
     * @param name
     */
    public static void runJavaApp(String name, String argument) {//String name 
        //
        SimpleLoggerLight11.logg("restarter.log", "Starting program = " + name);
        SimpleLoggerLight11.logg("restarter.log", "argument = " + argument);
        String[] commands2 = {"java", "-jar", name, argument};
        //
        try {
            Process p = Runtime.getRuntime().exec(commands2);
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(RestarterSimpleAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
