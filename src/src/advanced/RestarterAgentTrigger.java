/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src.advanced;

import com.jezhumble.javasysmon.JavaSysMon;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * NOTE: THIS CLASS IS DOES NOT AFFECT THE PROGRESTARTER, ITS ONLY HERE TO REMEMBER!
 * Add this class to any Program which needs to be monitored(turns the program on if it crashes).
 * @author Administrator
 */
public class RestarterAgentTrigger implements Runnable {

    private static ServerSocket serverSocket;
    private static Socket socket;
    private static int RESTARTER_PID;
    public static boolean RESTARTER_IN_USE = false;
    //=====================================================
    //all of this variables below are sent to the "RestarterAdvancedAgent" as arguments
    private static int PORT = 4444;//NOTE EMPTY ARGS CAUSES ERRORS!!!!!
    /**
     * Note write only the image name without path & extenssion!!!!!
     * the folder with images is ment to be images & the extenssion
     * MUST be .PNG
     */
    public static String PATH_TO_TRAY_ICON = "img_name"; //NOTE EMPTY ARGS CAUSES ERRORS!!!!!
    public static String PROGRAM_NAME = "My Application";//NOTE EMPTY ARGS CAUSES ERRORS!!!!!
    public static String PROGRAM_NAME_RUN = "myapplication.jar";//NOTE EMPTY ARGS CAUSES ERRORS!!!!!
    /**
     * Accepts only one arg. Doesnt accept empty args!!! type
     * at least 0
     */
    public static String PROGRAM_ARG = "0";//NOTE EMPTY ARGS CAUSES ERRORS!!!!!
    /**
     * Accepts only true/false
     */
    public static String TRAY_ENABLED = "true";//NOTE EMPTY ARGS CAUSES ERRORS!!!!!

    private void runCrashRestarter() {
        RESTARTER_IN_USE = true;
        //
        JavaSysMon monitor = new JavaSysMon();
        int pid = monitor.currentPid();
        //
        String[] commands2 = {"java", "-jar", "progrestarter.jar",
            "" + pid, "" + PORT, PATH_TO_TRAY_ICON, PROGRAM_NAME, PROGRAM_NAME_RUN,PROGRAM_ARG,TRAY_ENABLED};
        try {
            Process p = Runtime.getRuntime().exec(commands2);
        } catch (IOException ex) {
            System.exit(0);
            Logger.getLogger(RestarterAgentTrigger.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            serverSocket = new ServerSocket(PORT);
//            serverSocket.setSoTimeout(20000);
            socket = serverSocket.accept();
            serverSocket.close();
            try {
                recive();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(RestarterAgentTrigger.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(RestarterAgentTrigger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Cannot be SYNCHRONIZED!
     */
    private void recive() throws IOException, ClassNotFoundException {
        //
        ObjectInputStream input;
        //
        input = new ObjectInputStream(socket.getInputStream());

        String restarter_pid = (String) input.readObject();
        System.out.println("Restarter PROCESS ID = " + restarter_pid);
        try {
            RESTARTER_PID = Integer.parseInt(restarter_pid);
        } catch (Exception ex) {
            Logger.getLogger(RestarterAgentTrigger.class.getName()).log(Level.SEVERE, null, ex);
        }
        //
        socket.close();
        //
    }

    /**
     * This method is to termiante the restarter agent 
     * i connection with the main program close or ...
     */
    public static void terminateRestarterAgent() {
        JavaSysMon monitor = new JavaSysMon();
        monitor.killProcess(RESTARTER_PID);
    }

    @Override
    public void run() {
        runCrashRestarter();
    }
}
