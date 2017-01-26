/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package program_starter;

import supplementary.Program;
import Logger.SimpleLoggerLight11;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import supplementary.HelpM;

/**
 *
 * @author KOCMOC
 */
public class ProgStarter_A {

    private Properties p = new Properties();
    private ArrayList<Program> list = new ArrayList();
    private final static String LOG_MAIN = "starter.log";
    private final static String PROPERTIES_PATH ="main.properties";

    public ProgStarter_A() {
        load_properties();
        define_programs_to_run();
    }

    public void go() throws IOException {
        
        if(HelpM.check_if_console_session("query.exe") == false){
            SimpleLoggerLight11.logg(LOG_MAIN, "Console session = false");
            System.exit(0);
        }else{
            SimpleLoggerLight11.logg(LOG_MAIN, "Console session = true");
        }
        
        for (Program prog : list) {
            try {
                SimpleLoggerLight11.logg(LOG_MAIN, prog.getProgram_name() + " --> " + prog.getPath());
                HelpM.run_application_exe_or_jar(prog.getProgram_name(), prog.getPath());
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
    }

    public static void main(String[] args)  {
        HelpM.err_output_to_file();
        //========
        ProgStarter_A starter_A = new ProgStarter_A();
        try {
            starter_A.go();
        } catch (IOException ex) {
            Logger.getLogger(ProgStarter_A.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
