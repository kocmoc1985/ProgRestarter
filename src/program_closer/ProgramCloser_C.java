/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package program_closer;

import Logger.SimpleLoggerLight11;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import program_starter.Standard;
import static program_starter.Standard.LOG_MAIN;
import supplementary.Program;
import supplementary.HelpM;
import supplementary.OtherInstanceRunning;

/**
 *
 * @author KOCMOC
 */
public class ProgramCloser_C extends Standard {

    private ArrayList<Program> list = new ArrayList();
    private String TIME_TURN_OF = p.getProperty("close_time", "23:59");

    public ProgramCloser_C(boolean runEmbeded) {
        super(runEmbeded);
        define_programs_to_close();
        startThread();
    }

    @Override
    public void A() {
        //
        new Thread(new OtherInstanceRunning(5555, "")).start();
        //
        if (HelpM.getLoggedInUserName().equals(USERNAME) == false) {
            SimpleLoggerLight11.logg(LOG_MAIN, "Username not as required: exiting");
            System.exit(0);
        }
        //
    }

    private void define_programs_to_close() {
        for (int i = 1; p.getProperty("program_to_close_" + i) != null; i++) {
            String prog_name = p.getProperty("program_to_close_" + i);
            if (prog_name.trim().isEmpty() == false) {
                list.add(new Program(prog_name));
            }
        }
    }

    public void go() throws IOException {
        //
        if (get_proper_time_same_format_on_all_computers().equals(TIME_TURN_OF)) {
            for (Program prog : list) {
                String programName = prog.getProgram_name();
                if (HelpM.processRunning(programName)) {
                    HelpM.terminate_process_no_external_apps_in_use(programName);
                    SimpleLoggerLight11.logg(LOG_MAIN, "Closed: " + programName);
                }
            }
        }else{
            System.out.println("TIME: " + get_proper_time_same_format_on_all_computers());
        }
        //
    }

    @Override
    public void run() {

        while (true) {
            //
            wait_(30000);
            //
            try {
                go();
            } catch (IOException ex) {
                Logger.getLogger(ProgramCloser_C.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static String get_proper_time_same_format_on_all_computers() {
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }

    private void wait_(int millis) {
        synchronized (this) {
            try {
                wait(millis);
            } catch (InterruptedException ex) {
                Logger.getLogger(ProgramCloser_C.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) {
        HelpM.err_output_to_file();
        ProgramCloser_C pc = new ProgramCloser_C(false);
    }
}
