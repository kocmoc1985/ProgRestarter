/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program_starter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import supplementary.HelpM;
import supplementary.SqlTypeNotSpecifiedException;
import supplementary.Sql_B;

/**
 * This module was initially done for SriLanka. This module implements function
 * of monitoring sql database. The Control porgram writes the watchdog to the sql db
 * and this module checks this watchdog. If the watchdog is not updated it ends
 * the control program
 * @author KOCMOC
 */
public class ProgStarter_D extends ProgStarter_C {

    private int AMMOUNT_OF_LINES;
    private String CONTROL_NAME_PREFIX;
    private String SQL_TYPE;
    private String HOST;
    private String PORT;
    private String DB_NAME;
    private String SQL_USER;
    private String SQL_PASS;

    public ProgStarter_D(boolean runEmbeded) {
        super(runEmbeded);
        load_add_props();
        startSqlMonitoringThreads();
    }

    private void load_add_props() {
        AMMOUNT_OF_LINES = Integer.parseInt(p.getProperty("ammount_of_lines", "2"));
        CONTROL_NAME_PREFIX = p.getProperty("control_name_prefix", "Control");
        SQL_TYPE = p.getProperty("sql_type", "mssql");
        HOST = p.getProperty("sql_host", "");
        PORT = p.getProperty("sql_port", "1433");
        DB_NAME = p.getProperty("sql_db_name", "");
        SQL_USER = p.getProperty("sql_user", "");
        SQL_PASS = p.getProperty("sql_pass", "");
    }

    private void startSqlMonitoringThreads() {
        //
        for (int i = 0; i < AMMOUNT_OF_LINES; i++) {
            Thread x = new Thread(new SqlWatchDogMonitoringThread((i + 1)));
            x.start();
        }
        //
    }

    class SqlWatchDogMonitoringThread implements Runnable {

        private final int LINE;
        private final Sql_B sql_b = new Sql_B(true, false);
        private int last = -1;
        private int act;

        public SqlWatchDogMonitoringThread(int line) {
            connect();
            this.LINE = line;
        }

        private void connect() {
            try {
                sql_b.connect(SQL_TYPE, HOST, PORT, DB_NAME, SQL_USER, SQL_PASS);
//                sql_b.connect(Sql_B.SQL_TYPE_MSSQL, "10.87.0.2", "1433", "Lanka", "sa", "");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ProgStarter_D.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(ProgStarter_D.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SqlTypeNotSpecifiedException ex) {
                Logger.getLogger(ProgStarter_D.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            //
            while (true) {
                //
                try {
                    if (check()) {
                        String progName = CONTROL_NAME_PREFIX + LINE + ".exe";
                        HelpM.terminate_process_no_external_apps_in_use(progName);
//                        SimpleLoggerLight11.logg("test.txt", "TERMINATE:" + progName + "  / LINE: " + LINE);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(ProgStarter_D.class.getName()).log(Level.SEVERE, null, ex);
                }
                //
                wait_(60000);
                //
            }
            //
        }

        private synchronized void wait_(int millis) {
            try {
                wait(millis);
            } catch (InterruptedException ex) {
                Logger.getLogger(ProgStarter_D.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /**
         *
         * @return true if hanging
         * @throws SQLException
         */
        private boolean check() throws SQLException {
            //
            String q = "select * from MC_WATCHDOG where Line=" + LINE;
            //
            ResultSet rs = sql_b.execute(q);
            //
            if (rs.next()) {
                int watchdog = rs.getInt("Counter");
                act = watchdog;
            }
            //
            if (last == act) {
//                SimpleLoggerLight11.logg("test.txt", "EQUALS: last: " + last + " / act: " + act + "  / LINE: " + LINE);
                System.out.println();
                return true;
            } else {
//                SimpleLoggerLight11.logg("test.txt", "NOT EQUALS: last: " + last + " / act: " + act + "  / LINE: " + LINE);
            }
            //
            last = act;
            //
            return false;
        }

    }

    public static void main(String[] args) {
        //
        HelpM.err_output_to_file();
        //========
        ProgStarter_D starter = new ProgStarter_D(false);
        //
    }

}
