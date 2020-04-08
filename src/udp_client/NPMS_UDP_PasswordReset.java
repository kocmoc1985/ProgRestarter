/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package udp_client;

import Logger.SimpleLoggerLight11;

/**
 * Used by MCRemote Server - the main idea to create an icon which triggers 
 * this simple module [2020-04-08]
 * @author KOCMOC
 */
public class NPMS_UDP_PasswordReset extends Client_UDP {
    
    public NPMS_UDP_PasswordReset(String ip, int port) {
        super(ip, port);
    }
    
    public static void main(String[] args) {
        NPMS_UDP_PasswordReset npmsudppr = new NPMS_UDP_PasswordReset("localhost", 65533);
        npmsudppr.prepareAndSendDatagram(UDPCommands.NPMS_RESET_PASSWORD_CMD);
        SimpleLoggerLight11.logg("udp_modules.log", "npms_udp_password_reset: " + UDPCommands.NPMS_RESET_PASSWORD_CMD);
    }
    
}
