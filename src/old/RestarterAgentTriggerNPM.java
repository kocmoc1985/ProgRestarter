/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package old;

import com.jezhumble.javasysmon.JavaSysMon;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * THIS IS AN OLD VERSION OF RESTARTER_AGENT_TRIGGER ESPECIALLY FOR THE NPM!!!
 * There are problems replacing this old instance of "RestarterAgentTriggerNPM" with a new
 * one, because there is function ready yet that lets an update of the restarter modul.
 * restarter.jar modul should be in the "lib" folder so it could be updated.
 * @author Administrator
 */
public class RestarterAgentTriggerNPM implements Runnable {

    private static ServerSocket serverSocket;
    private static Socket socket;
    private static int PORT = 4444;
    private static int RESTARTER_PID;
    public static boolean RESTARTER_IN_USE = false;

    private void runCrashRestarter() {
        RESTARTER_IN_USE = true;
        JavaSysMon monitor = new JavaSysMon();
        int pid = monitor.currentPid();
        monitor = null;
        String[] commands2 = {"java", "-jar", "restarter.jar", "" + pid};
        try {
            Process p = Runtime.getRuntime().exec(commands2);
        } catch (IOException ex) {
            System.exit(0);
            Logger.getLogger(RestarterAgentTriggerNPM.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            serverSocket = new ServerSocket(PORT);
            socket = serverSocket.accept();
            serverSocket.close();
            try {
                recive();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(RestarterAgentTriggerNPM.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(RestarterAgentTriggerNPM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Cannot be SYNCHRONIZED!
     * 
     */
    private void recive() throws IOException, ClassNotFoundException {
        ObjectInputStream input = null;
        input = new ObjectInputStream(socket.getInputStream());

        String restarter_pid = (String) input.readObject();
        System.out.println("Restarter PROCESS ID = " + restarter_pid);
        try {
            RESTARTER_PID = Integer.parseInt(restarter_pid);
        } catch (Exception ex) {
            Logger.getLogger(RestarterAgentTriggerNPM.class.getName()).log(Level.SEVERE, null, ex);
        }
        socket.close();
        input = null;
    }

    public static void terminateRestarterAgent() {
        JavaSysMon monitor = new JavaSysMon();
        monitor.killProcess(RESTARTER_PID);
    }

    public void run() {
        runCrashRestarter();
    }
}
