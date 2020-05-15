/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src.advanced;

import Logger.SimpleLoggerLight11;
import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.ProcessInfo;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import supplementary.HelpM;
import udp.Server_UDP;
import udp.ShowMessage;

/**
 * This an advanced Restarter agent with abilities to monitor(it can restart the
 * MainModul if it crashes) the "TriggerModul" namely the "MainProgram" which
 * should be monitored. Also It implements Client functions to inform the
 * "MainModul" about it's ID so the "MainModul" shoul be able to switch of the
 * RestarterAgent.
 *
 * @author Administrator
 */
public class RestarterAdvancedAgent {

    private static CR RestarterAgentCR;

    public static void main(String[] args) {
        //Introduced on [2020-05-15]
        if (HelpM.runningInNetBeans()==false) {
            HelpM.err_output_to_file();
        }
        //
        if (args.length == 0) {
            System.exit(0);
        }

        int pid = Integer.parseInt(args[0]);
        int port = Integer.parseInt(args[1]);
        String trayIconPath = args[2];
        String programName = args[3];
        String programNameRun = args[4];
        String programArg = args[5];
        boolean trayEnabled = Boolean.parseBoolean(args[6]);
//        JOptionPane.showMessageDialog(null, "CR running: pid_recieved = " + npm_pid);
        RestarterAgentCR = new CR(pid, port, trayIconPath, programName, programNameRun, programArg, trayEnabled);
    }

    public static void restartNpms() {
        RestarterAgentCR.restartNpms();
    }

}

/**
 * IS [***CLIENT***]
 *
 * @author KOCMOC
 */
class CR implements Runnable {

    private int NPM_PORT;
    private JavaSysMon monitor = new JavaSysMon();
    private int npm_pid;
    private int restarter_pid;
    //================================
    private Socket socket;
    private Image img = null;
    private PopupMenu popup;
    private MenuItem exit;
    private SystemTray tray;
    private TrayIcon trayIcon;
    private String trayIconPath; //is recieved from the RestarterAgentTrigger
    private String programName; //is recieved from the RestarterAgentTrigger
    private String programNameRun; //is recieved from the RestarterAgentTrigger
    private String programArgument; //is recieved from the RestarterAgentTrigger
    private boolean trayEnabled; //is recieved from the RestarterAgentTrigger
    public static String LOGG_FILE = "restarter.log";

    public CR(int pid, int port, String trayIconPath,
            String programName, String programNameRun, String argument, boolean trayEnabled) {
        this.NPM_PORT = port;
        this.trayIconPath = trayIconPath;
        this.programName = programName;
        this.programNameRun = programNameRun;
        this.programArgument = argument;
        this.trayEnabled = trayEnabled;
        this.npm_pid = pid;
        //
        String msg = "port: " + port + " "
                + "progName: " + programName + " "
                + "progToRun: " + programNameRun + " "
                + "progArg: " + argument;
        //
        SimpleLoggerLight11.logg(LOGG_FILE, msg);
        //
        toTray();
        //
        //
        if (giveNpmMyPid()) {
            startThread(); // Starting TCP CLIENT
            startUdpServer(); // Starting UDP SERVER
        } else {
            System.exit(0);
        }
    }

    private void startUdpServer() {
        ShowMessage out = new ShowMessage() {
            @Override
            public void showMessage(String str) {
                SimpleLoggerLight11.logg("restarter_udp.log", str);
            }
        };
        ServerProtocolRestarterAdvAgent spraa = new ServerProtocolRestarterAdvAgent(out);
        Server_UDP server_UDP = new Server_UDP(65534, spraa, out);
    }

    private void startThread() {
        Thread x = new Thread(this);
        x.start();
    }

    @Override
    public void run() {
        while (true) {
            if (programRunning() == false) {
                // note '1' means auto start for the netprocessmonitor client part
                //for the server part it means start without checking the sessions
                runJavaApp(programNameRun, programArgument);
                System.exit(0);
            } else {
                wait_(10000);
            }
        }
    }

    private boolean giveNpmMyPid() {
        try {
            restarter_pid = monitor.currentPid();
            connect();
            ObjectOutputStream output;
            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject("" + restarter_pid);
            //
            SimpleLoggerLight11.logg(LOGG_FILE, "Gave NPMS my pid: " + restarter_pid);
            //
            output.flush();
            return true;
        } catch (Exception ex) {
            SimpleLoggerLight11.logg(LOGG_FILE, "Failed to give NPMS my pid: " + restarter_pid);
            Logger.getLogger(RestarterAdvancedAgent.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    private void connect() {
        //
        int timeout = 200;
        //
        for (int i = 0; i < 10; i++) {
            try {
                socket = new Socket("localhost", NPM_PORT);
                SimpleLoggerLight11.logg(LOGG_FILE, "Connection to NPMS on port: " + NPM_PORT + " succeded");
                break;
            } catch (Exception ex) {
                wait_(timeout);
                timeout += 200;
                SimpleLoggerLight11.logg(LOGG_FILE, "Connection to NPMS on port: " + NPM_PORT + " failed");
                Logger.getLogger(CR.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private synchronized void wait_(int millis) {
        try {
            wait(millis);
        } catch (InterruptedException ex) {
            Logger.getLogger(CR.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Runs a java app
     *
     * @param name
     */
    private void runJavaApp(String name, String argument) {//String name 
        //
        String[] commands2 = {"java", "-jar", name, argument};
        //
        try {
            Process p = Runtime.getRuntime().exec(commands2);
        } catch (Exception ex) {
            SimpleLoggerLight11.logg(LOGG_FILE, "Failed to run program: " + name + "  argument: " + argument);
            Logger.getLogger(RestarterAdvancedAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean programRunning() {
        //
        ProcessInfo[] pinfo = monitor.processTable();
        //
        for (ProcessInfo info : pinfo) {
            if (info.getPid() == npm_pid) {
                return true;
            }
        }
        return false;
    }

    public void restartNpms() {
        monitor.killProcess(npm_pid);
    }

    private void toTray() {
        if (trayEnabled == false) {
            return;
        }
        if (SystemTray.isSupported()) {

            tray = SystemTray.getSystemTray();
            img = Toolkit.getDefaultToolkit().getImage("images/" + trayIconPath + ".png");

            ActionListener actionListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() == exit) {
                        monitor.killProcess(npm_pid);
                        System.exit(0);
                    }
                }
            };

            popup = new PopupMenu();
            exit = new MenuItem("Shut down " + programName);

            exit.addActionListener(actionListener);

            popup.add(exit);
            trayIcon = new TrayIcon(img, programName + " agent", popup);

            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(actionListener);

            try {
                tray.add(trayIcon);
            } catch (Exception e) {
                Logger.getLogger(RestarterAdvancedAgent.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
//    public static void main(String[] args) {
//        String[] arr = {"2920"};
//        RestarterAdvancedAgent.main(arr);
//    }
}
