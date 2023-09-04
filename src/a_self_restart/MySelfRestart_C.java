/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a_self_restart;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author KOCMOC
 */
public class MySelfRestart_C implements Runnable {

    private final String RESTART_TIME_HH_MM;

    public MySelfRestart_C(String RESTART_TIME_HH_MM) {
        this.RESTART_TIME_HH_MM = RESTART_TIME_HH_MM;
    }

    public static void main(String[] args) {
        MySelfRestart_C c = new MySelfRestart_C("10:57");
        c.startThread();
        c.forTesting();
    }

    private void startThread() {
        Thread x = new Thread(this);
        x.start();
    }

    private void forTesting() {
        JFrame frame = new JFrame("Test self restarting: " + RESTART_TIME_HH_MM);
        frame.setSize(400, 400);
        frame.setVisible(true);
    }

    @Override
    public void run() {
        //
        while (true) {
            //
            wait_(60000); // 60000 is the minimum time, less is not good 
            //
            String curr_time = get_proper_time_same_format_on_all_computers();
            //
            if (curr_time.equals(RESTART_TIME_HH_MM)) {
                try {
                    restartApplication_with_output();
                } catch (URISyntaxException ex) {
                    Logger.getLogger(MySelfRestart_C.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MySelfRestart_C.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        //
    }

    private static String get_proper_time_same_format_on_all_computers() {
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }

    private static void wait_(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Logger.getLogger(MySelfRestart_C.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void restartApplication_with_output() throws URISyntaxException, IOException {
        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        final File currentJar = new File(MySelfRestart_C.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        //
        System.out.println("javaBin: " + javaBin + "\n\n");
        System.out.println("currentJar: " + currentJar + "\n\n");
        //
        /* is it a jar file? */
        if (!currentJar.getName().endsWith(".jar")) {
            System.out.println("Is it jar file: " + currentJar + " / No it's not" + "\n\n");
            return;
        }
        //
        // Separate thread is needed so the "information text" can be shown with the AWT-Thread
        Thread x = new Thread(new Runnable() {
            @Override
            public void run() {
                //
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MySelfRestart_C.class.getName()).log(Level.SEVERE, null, ex);
                }
                //
                /* Build command: java -jar application.jar */
                final ArrayList<String> command = new ArrayList<String>();
                command.add(javaBin);
                command.add("-jar");
                command.add(currentJar.getPath());
                final ProcessBuilder builder = new ProcessBuilder(command);
                try {
                    builder.start();
                } catch (IOException ex) {
                    Logger.getLogger(MySelfRestart_C.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.exit(0);
            }
        });
        //
        x.start();

    }

}
