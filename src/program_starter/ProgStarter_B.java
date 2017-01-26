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
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import supplementary.GP;
import supplementary.HelpM;
import supplementary.Program;

/**
 *
 * @author KOCMOC
 */
public class ProgStarter_B implements Runnable {

    private Properties p = new Properties();
    private ArrayList<Program> list = new ArrayList();
    private final static String LOG_MAIN = "starter.log";
    private final static String PROPERTIES_PATH = "main.properties";
    private PopupMenu popup;
    private MenuItem exit;
    private SystemTray tray;
    private TrayIcon trayIcon;
    private JavaSysMon monitor = new JavaSysMon();
    private int INTERVALL;
    private String TRAY_INFO;
    private boolean SHOW_TRAY;

    public ProgStarter_B() {
        load_properties();
        try {
            checkIfConsoleSession();
        } catch (IOException ex) {
            Logger.getLogger(ProgStarter_B.class.getName()).log(Level.SEVERE, null, ex);
        }
        define_programs_to_run();
        startThread();
        //
        if (SHOW_TRAY) {
            toTray();
        }
        //
        SimpleLoggerLight11.logg(LOG_MAIN, "started, pid: " + monitor.currentPid());
        SimpleLoggerLight11.logg(LOG_MAIN, "show tray: " + SHOW_TRAY);
    }

    private void startThread() {
        Thread x = new Thread(this);
        x.start();
    }

    private void checkIfConsoleSession() throws IOException {
        if (HelpM.check_if_console_session("query.exe") == false) {
            SimpleLoggerLight11.logg(LOG_MAIN, "Console session = false");
            System.exit(0);
        } else {
            SimpleLoggerLight11.logg(LOG_MAIN, "Console session = true");
        }
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

    private void go() throws IOException {

        for (Program prog : list) {
            try {
                if (HelpM.processRunning(prog.getProgram_name()) == false) {
                    HelpM.run_application_exe_or_jar(prog.getProgram_name(), prog.getPath());
                    SimpleLoggerLight11.logg(LOG_MAIN, prog.getProgram_name() + " --> " + prog.getPath());
                }
            } catch (IOException ex) {
                Logger.getLogger(ProgStarter_A.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void define_programs_to_run() {
        for (int i = 1; p.getProperty("program_to_run_" + i) != null; i++) {
            String[] path_and_prog_name = p.getProperty("program_to_run_" + i).split(";");
            String prog_name = path_and_prog_name[0];
            String path = path_and_prog_name[1];
            list.add(new Program(prog_name, path));
        }
    }

    private void load_properties() {
        p = HelpM.properties_load_properties(PROPERTIES_PATH);
        INTERVALL = Integer.parseInt(p.getProperty("intervall_min", "1"));
        TRAY_INFO = p.getProperty("tray_info", "MCStarter");
        SHOW_TRAY = Boolean.parseBoolean(p.getProperty("show_tray", "true"));
    }

    public static void main(String[] args) {
        //
        HelpM.err_output_to_file();
        //========
        ProgStarter_B starter_B = new ProgStarter_B();
    }

    @Override
    public void run() {
        while (true) {
            try {
                go();
            } catch (IOException ex) {
                Logger.getLogger(ProgStarter_B.class.getName()).log(Level.SEVERE, null, ex);
            }
            wait_(INTERVALL * 60000);
        }
    }

    private synchronized void wait_(int millis) {
        try {
            wait(millis);
        } catch (InterruptedException ex) {
            Logger.getLogger(ProgStarter_B.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
