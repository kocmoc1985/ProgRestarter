/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package program_closer;

import Logger.SimpleLoggerLight11;
import com.jezhumble.javasysmon.JavaSysMon;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import supplementary.Program;
import supplementary.HelpM;

/**
 *
 * @author KOCMOC
 */
public class ProgramCloser implements Runnable {

    private Properties p = new Properties();
    private ArrayList<Program> list = new ArrayList();
    private final static String LOG_MAIN = "closer.log";
    private final static String PROPERTIES_PATH = "main.properties";
    private int INTERVAL_MIN = 0;
    private final JavaSysMon monitor = new JavaSysMon();

    public ProgramCloser() {
        SimpleLoggerLight11.logg(LOG_MAIN, "ProgramCloser started. Pid: " + monitor.currentPid());
        load_properties();
        try {
            checkIfConsoleSession();
        } catch (IOException ex) {
            Logger.getLogger(ProgramCloser.class.getName()).log(Level.SEVERE, null, ex);
        }
        define_programs_to_run();
        start_thread();
    }
    
    private void load_properties() {
        p = HelpM.properties_load_properties(PROPERTIES_PATH);
        INTERVAL_MIN = Integer.parseInt(p.getProperty("intervall_min", "2")) * 60000;
    }
    
    private void checkIfConsoleSession() throws IOException {
        if (HelpM.check_if_console_session("query.exe") == false) {
            SimpleLoggerLight11.logg(LOG_MAIN, "Console session = false");
            System.exit(0);
        } else {
            SimpleLoggerLight11.logg(LOG_MAIN, "Console session = true");
        }
    }

    private void define_programs_to_run() {
        
        for (int i = 1; p.getProperty("program_to_close_" + i) != null; i++) {
            String prog_name = p.getProperty("program_to_close_" + i);
            list.add(new Program(prog_name));
        }
    }
    
    private void start_thread(){
        Thread x = new Thread(this);
        x.start();
    }

    public void go() throws IOException {
        SimpleLoggerLight11.logg(LOG_MAIN, "Performing check");
        for (Program prog : list) {
            String programName = prog.getProgram_name();
            if (HelpM.processRunning(programName)) {
                HelpM.terminate_process_no_external_apps_in_use(programName);
                SimpleLoggerLight11.logg(LOG_MAIN, "Closed: " + programName);
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            wait_(INTERVAL_MIN);
            try {
                go();
            } catch (IOException ex) {
                Logger.getLogger(ProgramCloser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void wait_(int millis) {
        synchronized (this) {
            try {
                wait(millis);
            } catch (InterruptedException ex) {
                Logger.getLogger(ProgramCloser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void main(String[] args) {
        HelpM.err_output_to_file();
        ProgramCloser pc = new ProgramCloser();
    }
}
