/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package old;

import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.ProcessInfo;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 * Restarter agent for the netprocmonitor ONLY!!
 * @author Administrator
 */
public class RestarterAdvancedAgentNPM {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.exit(0);
        }

        if (args.length == 2) {
            if (args[1] == null) {
                args[1] = "0"; // I set the arg to '0' because it doesnt take an empty arg!!!!! I spent a lot of time with this shit
            }
        }
        int pid = Integer.parseInt(args[0]);
//        JOptionPane.showMessageDialog(null, "CR running: pid_recieved = " + npm_pid);
        CRb crb = new CRb(pid);
    }
}

class CRb implements ActionListener {

    private int NPM_PORT = 4444;
    private Timer timer = new Timer(10000, this);
    private JavaSysMon monitor = new JavaSysMon();
    private int npm_pid;
    private int restarter_pid;
    //================================
    private Image img = null;
    private PopupMenu popup;
    private MenuItem exit;
    private SystemTray tray;
    private TrayIcon trayIcon;

    public CRb(int pid) {
//        toTray();
        try {
            giveNpmMyPid();
        } catch (IOException ex) {
            Logger.getLogger(CRb.class.getName()).log(Level.SEVERE, null, ex);
        }
        timer.start();
        this.npm_pid = pid;
        synchronized (this) {
            try {
                wait(); // this is important it prevents the program from runing to end (exiting)
            } catch (InterruptedException ex) {
                Logger.getLogger(CRb.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void giveNpmMyPid() throws IOException {
        restarter_pid = monitor.currentPid();
        Socket socket = new Socket("localhost", NPM_PORT);
        ObjectOutputStream output = null;
        output = new ObjectOutputStream(socket.getOutputStream());
        output.writeObject("" + restarter_pid);
        output.flush();
    }

    /**
     * Runs a java app
     * @param name
     */
    private void runJavaApp(String name, String argument) {//String name 

        String[] commands2 = {"java", "-jar", name, argument};
        try {
            Process p = Runtime.getRuntime().exec(commands2);
        } catch (IOException ex) {
            Logger.getLogger(RestarterAdvancedAgentNPM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean programRunning() {
        ProcessInfo[] pinfo = monitor.processTable();
        for (ProcessInfo info : pinfo) {
            if (info.getPid() == npm_pid) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (programRunning() == false) {
            // note '1' means auto start for the netprocessmonitor client part
            //for the server part it means start without checking the sessions
            runJavaApp("netprocmonitor.jar", "1"); 
            System.exit(0);
        }
    }

//    private void toTray() {
//        if (SystemTray.isSupported()) {
//
//            tray = SystemTray.getSystemTray();
//            img = Toolkit.getDefaultToolkit().getImage("images/client.png");
//
//            ActionListener actionListener = new ActionListener() {
//
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    if (e.getSource() == exit) {
//                        monitor.killProcess(npm_pid);
//                        System.exit(0);
//                    }
//                }
//            };
//
//            popup = new PopupMenu();
//            exit = new MenuItem("Shut down MCPM");
//
//            exit.addActionListener(actionListener);
//
//            popup.add(exit);
//            trayIcon = new TrayIcon(img, "MCPM agent", popup);
//
//            trayIcon.setImageAutoSize(true);
//            trayIcon.addActionListener(actionListener);
//
//            try {
//                tray.add(trayIcon);
//
//            } catch (AWTException e) {
//                System.err.println("TrayIcon could not be added.");
//            }
//        }
//    }
//    public static void main(String[] args) {
//        String[] arr = {"2920"};
//        RestarterAdvancedAgentNPM.main(arr);
//    }
}
