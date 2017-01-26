/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src.simple;

import Logger.FileLogger;
import Logger.SimpleLoggerLight11;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import supplementary.HelpM;

/**
 * USED BY %MILLS_BROWSER% & BY %NET_PROC_MON% IS ALSO CALLED UPDATER This will
 * suit my updated update mechanisms. This suits NetProcMonitor & MillsBrowser &
 * those projects that uses the update technology
 *
 * The Example of how to run the UPDATER -> where "name" is the name of program
 * to restart & "arg" argument 
 * private static void run_java_app(String name,
 * String arg) { wait_(100); String[] commands2 = {"java", "-jar",
 * "updater.jar", name, arg}; try { Process p =
 * Runtime.getRuntime().exec(commands2); } catch (IOException ex) {
 * Logger.getLogger(UpdateNotifier.class.getName()).log(Level.SEVERE, null, ex);
 * } }
 *
 * @author Administrator
 */
public class RestarterSimpleAgentB extends FileLogger {

    private static String LOG_FILE = "updater.log";

    public static void main(String[] args) {
        HelpM.err_output_to_file();
        HelpM.delete_file(LOG_FILE);
        //=====================
        if (args.length == 0) {
            System.exit(0);
        }

        if (args.length == 2) {
            if (args[1] == null) {
                args[1] = "0"; // I set the arg to '0' because it doesnt take an empty arg!!!!! I spent a lot of time with this shit
            } else {
                if (args[1].equals("restart")) {
                    //This to be able to restart client with help of updater (RestarterSimpleAgentB) instead of ProgRestarter (RestarterAdvanced...)
                    restartNetProcMonClientOnButtonPress(args);
                }
            }
        }

        wait_(3000);

        RestarterSimpleAgentB agentB = new RestarterSimpleAgentB();

        SimpleLoggerLight11.logg(LOG_FILE, "main() -> arg1 = " + args[0] + " / arg2 = " + args[1]);
        agentB.go(args[0], args[1]);

    }

    /**
     * For NetProcMonOnly
     *
     * @param args
     */
    private static void restartNetProcMonClientOnButtonPress(String[] args) {
        args[1] = "2";
        wait_(3000);
        RestarterSimpleAgentB agentB = new RestarterSimpleAgentB("");
        agentB.go(args[0], args[1]);
        wait_(100);
        System.exit(0);
    }
//    private JavaSysMon monitor = new JavaSysMon(); // Dont use with moduls which dont use libraries as tex. Executor client modul
    private String ARGUMENT_1 = "";
    private String ARGUMENT_2 = "";

    public RestarterSimpleAgentB() {
        super("RestarterSimpleAgentB");
        SimpleLoggerLight11.logg(LOG_FILE, "Constructor-1");
    }

    public RestarterSimpleAgentB(String a) {
        super("RestarterSimpleAgentB");
        SimpleLoggerLight11.logg(LOG_FILE, "Constructor-2");
    }

    private static void wait_(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Logger.getLogger(RestarterSimpleAgentB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * The main program is recieved as the program name with "_new.jar". The
     * updater remowes the old one & than renames the new one to the same name
     * as the old one had. For %MILLS_BROWSER% Some actions with the update file
     */
    private void delete_old_rename_new(String program_name) {
        int try_ = 0;
        program_name = program_name.replace(".jar", "");
        File f0 = new File(program_name + "_new.jar");
        if (f0.exists()) {
            File f = new File(program_name + ".jar");
            while (f.exists()) {
                f.delete();
                wait_(1000);
                try_++;
                SimpleLoggerLight11.logg(LOG_FILE, "delete_old_rename_new() -> " + "delete file failed " + try_);
                if (try_ >= 10) {
                    SimpleLoggerLight11.logg(LOG_FILE, "delete_old_rename_new() -> " + "Cannot perform update " + program_name + " is in use!");
                    break;
                }
            }
            f0.renameTo(new File(program_name + ".jar"));
            wait_(1000);
        } else {
            SimpleLoggerLight11.logg(LOG_FILE, "delete_old_rename_new() -> " + program_name + ".jar does not exist");
        }
    }

    /**
     *
     * @param arg1 = program name
     * @param arg2 = argument
     *
     */
    public void go(String arg1, String arg2) {
        ARGUMENT_1 = arg1;
        ARGUMENT_2 = arg2;

        if (ARGUMENT_1.contains(".jar") && ARGUMENT_2.equals("clear_update")) { // this means that the executor will be launched with "clear_update" arg which means that the program will delete all files except those which belong to executor
            runJavaApp(ARGUMENT_1, ARGUMENT_2);
        } else if (ARGUMENT_1.contains(".jar") && ARGUMENT_2.equals("delete_clear_update")) { // for deleting of "clear_update" file
            HelpM.deleteFile2("clear_update.adm");//I must delete the clear update file
            runJavaApp(ARGUMENT_1, "skip_update"); //and then run program with skip_update argument
        } else if (ARGUMENT_1.contains(".jar") && ARGUMENT_2.equals("skip_update")) { // updating of program
            delete_old_rename_new(ARGUMENT_1);

//        wait_(60001); //needed for the "Backuper" modul of MCAutoStarter, otherwise it will start and do backup aggain as the time is still the same!!
            runJavaApp(ARGUMENT_1, ARGUMENT_2);
        } else if (ARGUMENT_1.contains(".exe") || ARGUMENT_1.contains(".jar")) {
//            run_application_exe(ARGUMENT_1);
            find_and_run_application(".", ARGUMENT_1);
        }
    }

//    /**
//     * For %NET_PROCESS_MONITOR% update
//     * Some actions with the update file
//     */
//    public void delete_old_rename_new() {
////        loggEventUserMessage("delete_old_rename_new()", "", "Entered method ok!");
//        int try_ = 0;
//        File f0 = new File("netprocmonitor_new.jar");
//        if (f0.exists()) {
//            loggEventUserMessage("delete_old_rename_new()", "", "netprocmonitor_new.jar exists = true");
//            File f = new File("netprocmonitor.jar");
//            while (f.exists()) {
//                f.delete();
//                wait_(1000);
//                try_++;
//                loggEventUserMessage("delete_old_rename_new()", "", "delete file failed " + try_);
//                if (try_ >= 5) {
//                    ARGUMENT_2 = "2"; //!!!! This should mean to start without update, because the netprocmonitor.jar cannot be deleted, probably because one of the lines is running
//                    loggEventUserMessage("delete_old_rename_new()", "", "Cannot perform update, netprocmonitor.jar is in use!");
//                    break;
//                }
//            }
//
//            f0.renameTo(new File("NetProcMonitor.jar"));
//            wait_(1000);
//        } else {
//            loggEventUserMessage("delete_old_rename_new()", "", "netprocmonitor_new.jar dont exists!");
//        }
//    }
    /**
     * Runs a java app
     *
     * @param name
     */
    public void runJavaApp(String name, String argument) {//String name 
        SimpleLoggerLight11.logg(LOG_FILE, "runJavaApp() -> " + "name = " + name + "  /  " + "argument = " + argument);
        String[] commands2 = {"java", "-jar", name, argument};
//         wait_(1000);
        try {
            Process p = Runtime.getRuntime().exec(commands2);
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(RestarterSimpleAgentB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void find_and_run_application(String path, String application_to_run_name) {
        File[] f = new File(path).listFiles();

        for (File file : f) {
            if (file.isDirectory()) {
                find_and_run_application(file.getPath(), application_to_run_name);
            } else if (file.getName().toLowerCase().trim().equals(application_to_run_name.toLowerCase().trim())) {
                try {
                    run_application_exe_or_jar(application_to_run_name, file.getParent());
                } catch (IOException ex) {
                    Logger.getLogger(RestarterSimpleAgentB.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void run_application_exe_or_jar(String application_to_run_name, String path) throws IOException {
        String[] commands = new String[3];
        if (application_to_run_name.contains(".jar")) {
            commands[0] = "java";
            commands[1] = "-jar";
            commands[2] = application_to_run_name;
        } else {
            commands[0] = path + "/" + application_to_run_name;
            commands[1] = "";
            commands[2] = "";
        }
        SimpleLoggerLight11.logg(LOG_FILE, "run_application_exe_or_jar -> app_name = " + application_to_run_name + " -> path " + path);
        ProcessBuilder builder = new ProcessBuilder(commands);
        builder.directory(new File(path));
        builder.start();
    }

    private static void run_application_exe(String path) {
//        path = "c:/documents and settings/administrator/my documents/progs/1.exe";
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(RestarterSimpleAgentB.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            Process p = Runtime.getRuntime().exec(path);
            System.exit(0);
        } catch (IOException ex) {
            System.out.println("" + ex);
        }
    }
}
