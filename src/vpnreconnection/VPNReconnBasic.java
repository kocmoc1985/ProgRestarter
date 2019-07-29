/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vpnreconnection;

import java.io.IOException;
import program_starter.Standard;

/**
 *
 * @author KOCMOC
 */
public class VPNReconnBasic extends Standard {

    public String HOST_TO_CHECK;

    public VPNReconnBasic(boolean runEmbeded) {
        super(runEmbeded);
        load_add_props();
    }


    private void load_add_props() {
        //
        HOST_TO_CHECK = p.getProperty("host_to_check", "127.0.0.1");
        //
    }

    public boolean ping2(String host) throws IOException, InterruptedException {
        //
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        //
        ProcessBuilder processBuilder = new ProcessBuilder("ping", isWindows ? "-n" : "-c", "1", host);
        Process proc = processBuilder.start();
        //
        int returnVal = proc.waitFor();
        return returnVal == 0;
        //
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

}
