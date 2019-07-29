/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vpnreconnection;

import Logger.SimpleLoggerLight11;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import supplementary.HelpM;

/**
 *
 * @author KOCMOC
 */
public class VPNReconnWH extends VPNReconnBasic{

    public VPNReconnWH(boolean runEmbeded) {
        super(runEmbeded);
    }
    
    @Override
    public void run() {
        //
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
                //
                if (ping2(HOST_TO_CHECK) == false) {
                    //
                    SimpleLoggerLight11.logg(LOG_MAIN, "VPN not online, reconnecting");
                    //
                    if (HelpM.file_exists("connectvpn.cmd")) {
                        HelpM.run_application_with_associated_application(new File("connectvpn.cmd"));
                    }
                }
                //
            } catch (IOException ex) {
                Logger.getLogger(VPNReconnWH.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(VPNReconnWH.class.getName()).log(Level.SEVERE, null, ex);
            }
            //
            wait_minutes(INTERVALL_MIN);
            //
        }
        //
    }
    
    public static void main(String[] args) {
        //
        HelpM.err_output_to_file();
        //
        VPNReconnWH vpnrwh = new VPNReconnWH(false);
        //
        vpnrwh.startThread();
    }
    
}
