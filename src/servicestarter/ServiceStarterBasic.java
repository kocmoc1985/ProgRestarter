/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicestarter;

import Logger.SimpleLoggerLight11;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import program_starter.Standard;
import supplementary.HelpM;
import supplementary.Program;

/**
 *
 * @author KOCMOC
 */
public class ServiceStarterBasic extends Standard {

    private ArrayList<Program> list = new ArrayList();
    private int LINE_COUNTER_POSITION;
    public final String PATH_TO_SC_APP = "c:/windows/system32/sc.exe";

    public ServiceStarterBasic(boolean runEmbeded) {
        super(false);
        load_add_props();
        define_services_to_monitor();
    }

    @Override
    public void run() {
        while (true) {
            //
            if (INITIAL_DELAY_ONE_TIME_FLAG == false) {
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
                Logger.getLogger(ServiceStarterBasic.class.getName()).log(Level.SEVERE, null, ex);
            }
            //
            wait_(INTERVALL_MIN * 60000);
            //
        }
    }

    private void load_add_props() {
        LINE_COUNTER_POSITION = Integer.parseInt(p.getProperty("line_counter_position", "3"));
    }

    private void go() throws IOException {
        //
        for (Program service : list) {
            //
            if (check_if_service_running(PATH_TO_SC_APP, service.getServiceName(), "STATE__NOT_IN_USE")) {
//                System.out.println("Service RUNNING: " + service.getServiceName());
            } else {
//                System.out.println("Service not running: " + service.getServiceName());
                run_service(PATH_TO_SC_APP, "start", service.getServiceName());
                SimpleLoggerLight11.logg(LOG_MAIN, "Service: " + service.getServiceName() + " not started, will now start");
            }
            //
        }
        //
    }

    private void define_services_to_monitor() {
        for (int i = 1; p.getProperty("service_to_monitor_" + i) != null; i++) {
            String[] path_and_prog_name = p.getProperty("service_to_monitor_" + i).split(";");
            if (path_and_prog_name.length < 2) {
                continue;
            }
            String prog_name = path_and_prog_name[0];
            String path = path_and_prog_name[1];
            list.add(new Program(prog_name, path));
        }
    }

    /**
     * Check if service running from cmd: *sc query serviceName* Pay attention
     * to the "lineCounter" which is used instead of "expressionToMatch". This
     * was done because it may not work when the language is not english
     *
     * @param path_to_executing_app
     * @param serviceName
     * @param expressionToMatch
     * @return
     * @throws IOException
     */
    public boolean check_if_service_running(String path_to_executing_app, String serviceName, String expressionToMatch) throws IOException {
        String[] cmd = {path_to_executing_app, "query", serviceName};//c:/windows/system32/sc.exe

        String line;
        InputStream stdout = null;

        // launch EXE and grab stdin/stdout and stderr
        Process process = Runtime.getRuntime().exec(cmd);
        stdout = process.getInputStream();

        // clean up if any output in stdout
        BufferedReader brCleanUp = new BufferedReader(new InputStreamReader(stdout));
        //
        int lineCounter = 0;
        //
        while ((line = brCleanUp.readLine()) != null) {
//                System.out.println("---------------------->" + line);
            if (lineCounter == LINE_COUNTER_POSITION) { //line.toLowerCase().contains(expressionToMatch.toLowerCase())
                if (line.contains("RUNNING")) { //RUNNING
//                    System.out.println("service = '" + serviceName + "' is running");
                    brCleanUp.close();
                    return true;
                }
            }
//            System.out.println("[Stdout] " + line);
            lineCounter++;
        }
        return false;
    }

    public void run_service(String path_to_program, String p1, String p2) {
        String[] cmd = {path_to_program, p1, p2};
        try {
            Process process = Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) {
            Logger.getLogger(ServiceStarterBasic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void A() {
        super.A(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void B() {
        super.B(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void C() {
        super.C(); //To change body of generated methods, choose Tools | Templates.
    }

    public static void main(String[] args) {
        //
        HelpM.err_output_to_file();
        //
        ServiceStarterBasic ssb = new ServiceStarterBasic(false);
        ssb.startThread();
        //
    }

}
