/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package program_starter;

import Logger.SimpleLoggerLight11;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import program_closer.ProgramCloser_C;
import supplementary.HelpM;
import supplementary.Program;

/**
 * Interval based check: starts the program if not running
 * OBS! Also includes a time based Closer
 * @author KOCMOC
 */
public class ProgStarter_C extends Standard {

    private ArrayList<Program> list = new ArrayList();
    private boolean ENABLE_CLOSER = false;

    public ProgStarter_C(boolean runEmbeded) {
        super(runEmbeded);
        define_programs_to_run();
        load_add_props();
        startCloser();
        startThread();
    }
    
    private void load_add_props(){
        ENABLE_CLOSER = Boolean.parseBoolean(p.getProperty("enable_closer_modul", "false"));
    }
    
    private void startCloser(){
        if(ENABLE_CLOSER){
            ProgramCloser_C c = new ProgramCloser_C(true);
        }
    }

    private void go() throws IOException {

        for (Program prog : list) {
            try {
                //
                boolean running;
                //
                if(DEFINE_WITH_QUERY){ // OBS the JavaSysmon lib not working for WIN2012 R2 SERVER
                    running = HelpM.processRunningB(prog.getProgram_name());
                }else{
                    running = HelpM.processRunning(prog.getProgram_name());
                }
                //
                if (running == false) {
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
            if(path_and_prog_name.length < 2){
                continue;
            }
            String prog_name = path_and_prog_name[0];
            String path = path_and_prog_name[1];
            list.add(new Program(prog_name, path));
        }
    }

    @Override
    public void run() {
        while (true) {
            //
            if(INITIAL_DELAY_ONE_TIME_FLAG == false){
                //
                wait_(INITIAL_DELAY_USED_AT_START_UP_MIN * 60000);
                //
                INITIAL_DELAY_ONE_TIME_FLAG = true;
                //
            }
            //
            try {
                go();
            } catch (IOException ex) {
                Logger.getLogger(ProgStarter_C.class.getName()).log(Level.SEVERE, null, ex);
            }
            wait_(INTERVALL_MIN * 60000);
        }
    }

    

    public static void main(String[] args) {
        //
        HelpM.err_output_to_file();
        //========
        ProgStarter_C starter_B = new ProgStarter_C(false);
    }
}
