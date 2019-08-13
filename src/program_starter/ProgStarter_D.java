/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program_starter;

import Logger.SimpleLoggerLight11;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import supplementary.HelpM;
import supplementary.SqlTypeNotSpecifiedException;
import supplementary.Sql_B;

/**
 *
 * @author KOCMOC
 */
public class ProgStarter_D extends ProgStarter_C {

    public ProgStarter_D(boolean runEmbeded) {
        super(runEmbeded);
        startSqlMonitoringThreads();
    }

    private void startSqlMonitoringThreads() {
        //
        Thread x1 = new Thread(new SqlWatchDogMonitoringThread(1));
        x1.start();
        //
        Thread x2 = new Thread(new SqlWatchDogMonitoringThread(2));
        x2.start();
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
                sql_b.connect(Sql_B.SQL_TYPE_MSSQL, "10.143.3.61", "1433", "Mixcont", "mixcont", "mixcont");
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
                        String progName = "Control" + LINE + ".exe";
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
