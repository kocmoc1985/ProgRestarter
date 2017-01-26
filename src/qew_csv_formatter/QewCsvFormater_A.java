/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qew_csv_formatter;

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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import program_starter.ProgStarter_B;
import supplementary.GP;
import supplementary.HelpM;

/**
 *
 * @author KOCMOC
 */
public class QewCsvFormater_A implements Runnable {

    private PopupMenu popup;
    private MenuItem exit;
    private SystemTray tray;
    private TrayIcon trayIcon;
    private JavaSysMon monitor = new JavaSysMon();
    private final static String LOG_MAIN = "formater.log";
    private Properties p = new Properties();
    private int INTERVALL;
    private String TRAY_INFO;
    private boolean SHOW_TRAY;
    private String CSV_FILEPATH;
    private final static String PROPERTIES_PATH = "main.properties";
    private long dateModifiedPrev = 0;

    public QewCsvFormater_A() {
        load_properties();
        //
        SimpleLoggerLight11.logg(LOG_MAIN, "formater, pid: " + monitor.currentPid());
        SimpleLoggerLight11.logg(LOG_MAIN, "show tray: " + SHOW_TRAY);
        //
//        try {
//            checkIfConsoleSession();
//        } catch (IOException ex) {
//            Logger.getLogger(QewCsvFormater_A.class.getName()).log(Level.SEVERE, null, ex);
//        }
        //
        toTray();
        //
        startThread();
    }

    public static void main(String[] args) {
        HelpM.err_output_to_file();
        new QewCsvFormater_A();
    }

    private void startThread() {
        Thread x = new Thread(this);
        x.start();
    }

    private void load_properties() {
        p = HelpM.properties_load_properties(PROPERTIES_PATH);
        CSV_FILEPATH = p.getProperty("csv_file_path", "woplanning.csv");
        INTERVALL = Integer.parseInt(p.getProperty("intervall_min", "1"));
        TRAY_INFO = p.getProperty("tray_info", "CsvFormater");
        SHOW_TRAY = Boolean.parseBoolean(p.getProperty("show_tray", "true"));
    }

    private void go() {
        File file = new File(CSV_FILEPATH);
        //
        if (file.exists() == false) {
            SimpleLoggerLight11.logg(LOG_MAIN, "file not found");
            return;
        }
        //
        long dateModified = file.lastModified();
        //
//        System.out.println("diff: " + (dateModified - dateModifiedPrev));
        //
        if (dateModified > (dateModifiedPrev)) {
            //
            String csv = read_Txt_To_String(CSV_FILEPATH, ";");
            //
            try {
                HelpM.copy_file(CSV_FILEPATH, defineOutPutName(CSV_FILEPATH,"_orig"));
                HelpM.writeToFile(CSV_FILEPATH, csv);
            } catch (IOException ex) {
                SimpleLoggerLight11.logg(LOG_MAIN, "write to file failed!");
                Logger.getLogger(QewCsvFormater_A.class.getName()).log(Level.SEVERE, null, ex);
            }
            //
            dateModifiedPrev = file.lastModified();
            //
        }
    }

    private String defineOutPutName(String filePath,String sufix){
        String[]arr = filePath.split("\\.");
        return arr[0] + sufix + "." + arr[1];
    }
    
    public static String read_Txt_To_String(String filename, String regex) {
        //
        String toReturn = "";
        //
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String[] parts;
            String rs = br.readLine();
            //
            while (rs != null) {
                //
                parts = rs.split(regex);
                //
                if (parts.length == 1 || parts.length == 0) {
                    rs = br.readLine();
                    continue;
                }
                //
                if (parts[1].equals("Artikel") == false) {
                    if (parts[1].contains("\"") == false) {
                        String code = "\"" + parts[1] + "\"";
                        rs = rs.replace(parts[1], code);
                    }
                }
                //

                toReturn += rs + "\n";

                //
                rs = br.readLine();
            }
            //
            br.close();
            //
        } catch (IOException e) {
//            System.out.println("" + e);
        }
        //
        
        //
        return toReturn;
    }

    @Override
    public void run() {
        while (true) {
            go();
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
}
