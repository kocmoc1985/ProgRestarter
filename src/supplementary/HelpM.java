/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package supplementary;

import Logger.SimpleLoggerLight11;
import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.ProcessInfo;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import static program_starter.Standard.LOG_MAIN;

/**
 *
 * @author KOCMOC
 */
public class HelpM {
    
    /**
     * SUPER GOOD Found [2020-04-29] Introduced in this Project [2020-05-15]
     *
     * @return
     */
    public static boolean runningInNetBeans() {
        //
        File currentJar = null;
        //
        try {
            currentJar = new File(HelpM.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException ex) {
            Logger.getLogger(HelpM.class.getName()).log(Level.SEVERE, null, ex);
        }
        //
        if (currentJar == null) {
            return false; // As it was running from ".jar" to make output to file
        }
        //
        /* is it a jar file? */
        if (!currentJar.getName().endsWith(".jar")) {
            return true;
        } else {
            return false;
        }
        //
    }

    public static void err_output_to_file() {
        //Write error stream to a file
        create_dir_if_missing("err_output_pr");
        try {
            String err_file = "err_" + get_date_time() + ".txt";
            String output_path = "err_output_pr/" + err_file;

            PrintStream out = new PrintStream(new FileOutputStream(output_path));
            System.setErr(out);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HelpM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void create_dir_if_missing(String path_and_folder_name) {
        File f = new File(path_and_folder_name);
        if (f.exists() == false) {
            f.mkdir();
        }
    }

    public static String get_date_time() {
        DateFormat formatter = new SimpleDateFormat("yyyy_MM_dd HH_mm_ss");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }

    ///=======================================================
    public static String get_proper_date_time_same_format_on_all_computers() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }

    public static void delete_file(String file_name) {
        File f = new File(file_name);
        f.delete();
    }

    public static boolean deleteFile2(String path) {
        File f = new File(path);
        for (int i = 0; i < 10; i++) {
            if (f.delete()) {
                return true;
            } else {
                wait_(100);
            }
        }
        return false;
    }

    /**
     *
     * @param path_andOr_fileName "onoff.properties" or
     * "c:/src/onoff.properties"
     * @param list_properties specifies if the properties should be listed
     * @return loaded properties
     */
    public static Properties properties_load_properties(String path_andOr_fileName) {
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(path_andOr_fileName));
        } catch (IOException ex) {
            Logger.getLogger(HelpM.class.getName()).log(Level.SEVERE, null, ex);
        }
        return p;
    }

    public static void run_application_exe_or_jar(String application_to_run_name, String path) throws IOException {
        String[] commands = new String[3];
        if (application_to_run_name.contains(".jar")) {
            commands[0] = "java";
            commands[1] = "-jar";
            commands[2] = application_to_run_name; //OBS! pay attention here
        } else {
            commands[0] = path + "/" + application_to_run_name; // and here!
            commands[1] = "";
            commands[2] = "";
        }
        ProcessBuilder builder = new ProcessBuilder(commands);
        builder.directory(new File(path));
        builder.start();
    }
    
    
     public static String getLoggedInUserName() {
        return System.getProperties().getProperty("user.name");
    }

    /**
     * uses query.exe
     *
     * @tested
     * @tags grab_output, grab_out_put, grab output, console
     * @param path_to_executing_app
     * @return
     * @throws IOException
     */
    public static boolean check_if_console_session(String path_to_executing_app) throws IOException {
        String[] cmd = {path_to_executing_app, "session"};//c:/windows/system32/query.exe

        String line;
        InputStream stdout;

        // launch EXE and grab stdin/stdout and stderr
        Process process = Runtime.getRuntime().exec(cmd);
        stdout = process.getInputStream();

        // clean up if any output in stdout
        BufferedReader brCleanUp = new BufferedReader(new InputStreamReader(stdout));
        while ((line = brCleanUp.readLine()) != null) {
            String pattern_1 = ">console"; //this means that this session belongs to my active session
            if (line.toLowerCase().contains(pattern_1.toLowerCase())) {
                return true;
            }
        }
        brCleanUp.close();
        return false;
    }

    public static boolean processRunning(String processName) {
        JavaSysMon monitor = new JavaSysMon();
        ProcessInfo[] pinfo = monitor.processTable();
        for (int i = 0; i < pinfo.length; i++) {
            String pname = pinfo[i].getName();
//            SimpleLoggerLight11.logg(LOG_MAIN, "win: " + pname + "  /my: " + processName);
            if (pname.toLowerCase().equals(processName.toLowerCase())) {
                return true;
            }

        }
        return false;
    }
    
    public static boolean processRunningB(String processName) throws IOException {
        String[] cmd = {"query.exe", "process"};//c:/windows/system32/query.exe

        String line;
        InputStream stdout;

        // launch EXE and grab stdin/stdout and stderr
        Process process = Runtime.getRuntime().exec(cmd);
        stdout = process.getInputStream();

        // clean up if any output in stdout
        BufferedReader brCleanUp = new BufferedReader(new InputStreamReader(stdout));
        while ((line = brCleanUp.readLine()) != null) {
            String pattern_1 = processName; //this means that this session belongs to my active session
            System.out.println("" +line.toLowerCase());
            if (line.toLowerCase().contains(pattern_1.toLowerCase())) {
                return true;
            }
        }
        brCleanUp.close();
        return false;
    }
    
    public static void main(String[] args) {
        try {
            System.out.println("running: " + processRunningB("quick.exe"));
        } catch (IOException ex) {
            Logger.getLogger(HelpM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void terminate_process_no_external_apps_in_use(String processName) {
        JavaSysMon monitor = new JavaSysMon();
        ProcessInfo[] pinfo = monitor.processTable();

        for (int i = 0; i < pinfo.length; i++) {
            String pname = pinfo[i].getName();
            int pid = pinfo[i].getPid();
             if (pname.toLowerCase().equals(processName.toLowerCase())) {
                monitor.killProcess(pid);
            }
        }
    }
    
    public static void copy_file(String file_to_copy, String name_of_duplicate) throws FileNotFoundException, IOException {
        File inputFile = new File(file_to_copy);
        File outputFile = new File(name_of_duplicate);

        FileInputStream in = new FileInputStream(inputFile);
        FileOutputStream out = new FileOutputStream(outputFile);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        //
        out.flush();
        in.close();
        out.close();
    }
    
     public static void writeToFile(String fileName, String textToWrite) throws IOException {
        FileWriter fstream = new FileWriter(fileName, false);
        BufferedWriter out = new BufferedWriter(fstream);
        //
        out.write(textToWrite);
        out.newLine();
        out.flush();
        out.close();
        fstream.close();
    }

    private static void wait_(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Logger.getLogger(HelpM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     public static boolean file_exists(String path) {
        File f = new File(path);
        return f.exists();
    }
    
    public static void run_application_with_associated_application(File file) throws IOException {
        Desktop.getDesktop().open(file);
    }
    
    
}
