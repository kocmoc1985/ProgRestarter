/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.advanced;

import udp.ServerProtocol_UDP;
import udp.ShowMessage;

/**
 *
 * @author KOCMOC
 */
public class ServerProtocolRestarterAdvAgent extends ServerProtocol_UDP{

    public static final String NPMS_RESTART = "#npms_restart#";
    
    public ServerProtocolRestarterAdvAgent(ShowMessage OUT) {
        super(OUT);
    }

    @Override
    public void handleRequest(String msg) {
        //
        OUT.showMessage(msg);
        //
        if (msg.contains(NPMS_RESTART)) {
           RestarterAdvancedAgent.restartNpms();
        }
        //
    }
    
    
    
}
