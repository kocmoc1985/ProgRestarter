/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author Administrator
 */
public final class LoggR {

    private final String PATH = "restarter_logg.txt";
    private final String PATH_EX = "restarter_exceptions.txt";
    private final String PATH_USRMSG = "restarter_varlogg.txt";
    private final boolean TRACING_ON = true;
    private final int MAX_LOGG_FILE_SIZE = 20;

    /**
     * For logging method visiting
     * @param actual_method
     * @param called_from
     * @param clas
     */
    public LoggR(String actual_method, String called_from, String clas) {
        if (TRACING_ON == false) {
            return;
        }

        String[] temp = {clas, actual_method, called_from, get_proper_date_and_time_default_format()};
        write_buffer_file(temp, PATH);
    }

    /**
     * For logging method visiting
     * @param actual_method
     * @param called_from
     * @param clas
     */
    public LoggR(String actual_method, String value_to_trace, String clas, String user_msg) {
        if (TRACING_ON == false) {
            return;
        }
        String[] temp = {clas, actual_method, user_msg, value_to_trace, get_proper_date_and_time_default_format()};
//        write_buffer_file(temp, PATH);
        write_buffer_file(temp, PATH_USRMSG);
    }

    /**
     * For Extended Exception Logging LEVEL 3
     * @param Exception
     */
    public LoggR(Exception exception, String clas, int line) {
        if (TRACING_ON == false) {
            return;
        }
        String[] temp = {clas, "Line: " + line, exception.toString(), get_proper_date_and_time_default_format()};
        write_buffer_file(temp, PATH_EX);
    }

    /**
     *
     * @param exception
     * @param clas
     * @param line
     */
    public LoggR(Throwable exception, String clas, int line) {
        if (TRACING_ON == false) {
            return;
        }
        String[] temp = {clas, "Line: " + line, exception.toString(), get_proper_date_and_time_default_format()};
        write_buffer_file(temp, PATH_EX);
    }

    /**
     * For Simple Exception Logging LEVEL 2
     * @param Exception
     */
    public LoggR(Exception exception, String clas) {
        if (TRACING_ON == false) {
            return;
        }
        String[] temp = {clas, exception.toString(), get_proper_date_and_time_default_format()};
        write_buffer_file(temp, PATH_EX);
    }

    /**
     * For Simple Exception Logging LEVEL 1
     * @param Exception
     */
    public LoggR(Exception exception) {
        if (TRACING_ON == false) {
            return;
        }
        String[] temp = {exception.toString(), get_proper_date_and_time_default_format()};
        write_buffer_file(temp, PATH_EX);
    }

    /**
     *
     * @param fileToWriteTO
     * @param signalArr
     */
    public void write_buffer_file(String[] signalArr, String file_name) {
        if (get_file_size_mb(file_name) > MAX_LOGG_FILE_SIZE) {
            File f = new File(file_name);
            f.delete();
//            System.out.println("logg_file_size " + MAX_LOGG_FILE_SIZE + " exceded the logging is inactive");
            return;
        }
        try {
            // Create file
            FileWriter fstream = new FileWriter(file_name, true);
            BufferedWriter out = new BufferedWriter(fstream);

            out.newLine();
            out.newLine();
            for (int i = 0; i < signalArr.length; i++) {
                if (signalArr[i] != null && i < (signalArr.length - 1)) {
                    out.write(signalArr[i] + "#");
                } else {
                    if (signalArr[i] != null) {
                        out.write(signalArr[i]);
                    }
                }
                out.flush();
            }

            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.out.println("Error: " + e.getMessage());
        }

    }

    /**
     * This method is the best one to get the local default time used on the computer
     * @return
     */
    public String get_proper_date_and_time_default_format() {
        TimeZone tz = TimeZone.getDefault();
        Calendar cal = Calendar.getInstance(tz);
        DateFormat f1 = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
        Date d = cal.getTime();
        return f1.format(d);
    }

    /**
     *
     * @param file_path
     */
    public double get_file_size_mb(String path) {
        File f = new File(path);
        return f.length() / 1048576;
    }
}
