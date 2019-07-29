/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package program_starter;

import Logger.SimpleLoggerLight11;
import com.jezhumble.javasysmon.JavaSysMon;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import supplementary.GP;
import supplementary.HelpM;

/**
 *
 * @author KOCMOC
 */
public class Standard implements Runnable {

    public Properties p = new Properties();
    public final static String LOG_MAIN = "starter.log";
    private final static String PROPERTIES_PATH = "main.properties";
    private PopupMenu popup;
    private MenuItem exit;
    private SystemTray tray;
    private TrayIcon trayIcon;
    private JavaSysMon monitor = new JavaSysMon();
    public int INITIAL_DELAY_USED_AT_START_UP_MIN;
    public boolean INITIAL_DELAY_ONE_TIME_FLAG = false;
    public int INTERVALL_MIN;
    public String USERNAME;
    public boolean START_CHECK_A;
    public boolean START_CHECK_B;
    public boolean START_CHECK_C;
    public boolean RUN_IN_CONSOLE;
    public boolean DEFINE_WITH_QUERY;
    public String TRAY_INFO;
    public boolean SHOW_TRAY;
    //

    public Standard(boolean runEmbeded) {
        load_properties();
        if (runEmbeded == false) {
            check();
        }

        //
        if (SHOW_TRAY && runEmbeded == false) {
            toTray();
            SimpleLoggerLight11.logg(LOG_MAIN, "started, pid: " + monitor.currentPid());
        }
    }

    private void check() {
        if (RUN_IN_CONSOLE) {
            try {
                isConsoleSession();
            } catch (IOException ex) {
                Logger.getLogger(ProgStarter_C.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (START_CHECK_A) {
            A();
        }

        if (START_CHECK_B) {
            B();
        }

        if (START_CHECK_C) {
            C();
        }
    }

    public void A() {
        //
//        new Thread(new OtherInstanceRunning(5555, "")).start();
//        //
//        if (HelpM.getLoggedInUserName().equals(USERNAME) == false) {
//            SimpleLoggerLight11.logg(LOG_MAIN, "Username not as required: exiting");
//            System.exit(0);
//        }
        //
    }

    public void B() {
    }

    public void C() {
    }

    public void startThread() {
        Thread x = new Thread(this);
        x.start();
    }

    @Override
    public void run() {
    }

    private void load_properties() {
        p = HelpM.properties_load_properties(PROPERTIES_PATH);
        INITIAL_DELAY_USED_AT_START_UP_MIN = Integer.parseInt(p.getProperty("initial_delay", "0"));
        INTERVALL_MIN = Integer.parseInt(p.getProperty("intervall_min", "1"));
        USERNAME = p.getProperty("username_session", "");
        START_CHECK_A = Boolean.parseBoolean(p.getProperty("start_check_a", "false"));
        START_CHECK_B = Boolean.parseBoolean(p.getProperty("start_check_b", "false"));
        START_CHECK_C = Boolean.parseBoolean(p.getProperty("start_check_c", "false"));
        RUN_IN_CONSOLE = Boolean.parseBoolean(p.getProperty("run_in_console", "false"));
        DEFINE_WITH_QUERY = Boolean.parseBoolean(p.getProperty("define_process_with_query", "false"));
        TRAY_INFO = p.getProperty("tray_info", "MCStarter");
        SHOW_TRAY = Boolean.parseBoolean(p.getProperty("show_tray", "true"));
    }

    private void isConsoleSession() throws IOException {
        if (HelpM.check_if_console_session("query.exe") == false) {
            SimpleLoggerLight11.logg(LOG_MAIN, "Console session = false");
            System.exit(0);
        } else {
            SimpleLoggerLight11.logg(LOG_MAIN, "Console session = true");
        }
    }
    
    public synchronized void wait_(int millis) {
        //
        if(millis == 0){
            millis = 1;
        }
        //
        try {
            wait(millis);
        } catch (InterruptedException ex) {
            Logger.getLogger(ProgStarter_C.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void wait_minutes(int minutes){
        wait_(minutes * 60000);
    }
    

    private void toTray() {
        if (SystemTray.isSupported()) {

            tray = SystemTray.getSystemTray();
            Image img = new ImageIcon(GP.IMAGE_ICON_URL).getImage();

            ActionListener actionListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() == exit) {
                        System.exit(0);
                    }
                }
            };

            popup = new PopupMenu();
            exit = new MenuItem("EXIT");

            exit.addActionListener(actionListener);

            popup.add(exit);

            trayIcon = new TrayIcon(img, TRAY_INFO + " (" + monitor.currentPid() + ")", popup);

            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(actionListener);

            try {
                tray.add(trayIcon);

            } catch (AWTException e) {
                System.err.println("TrayIcon could not be added.");
            }
        }
    }
}
