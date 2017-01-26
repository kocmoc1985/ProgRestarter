/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src.simple;

import Logger.SimpleLoggerLight11;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class RestarterSimpleAgent {

    public static void main(String[] args) {

        //Note!!!
        //You must have the second parameter, otherwise will not work
        //String[] commands2 = {"java", "-jar", "restarter.jar", "OPCRecorderParalel.jar","0"};
        if (args.length == 0) {
            System.exit(0);
        }



        if (args.length == 2) {
            if (args[1] == null) {
                args[1] = "0"; // I set the arg to '0' because it doesnt take an empty arg!!!!! I spent a lot of time with this shit
            }
            runJavaApp(args[0], args[1]);
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
        SimpleLoggerLight11.logg("restarter.log", "Starting program = " + name);
        SimpleLoggerLight11.logg("restarter.log", "argument = " + argument);
        String[] commands2 = {"java", "-jar", name, argument};
        try {
            Process p = Runtime.getRuntime().exec(commands2);
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(RestarterSimpleAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
