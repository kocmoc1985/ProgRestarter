/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package supplementary;

/**
 *
 * @author KOCMOC
 */
public class Program {

    private String program_name;
    private String path;

    public Program(String program_name, String path) {
        this.program_name = program_name;
        this.path = path;
    }

    public Program(String program_name) {
        this.program_name = program_name;
    }

    public String getPath() {
        return path;
    }

    public String getProgram_name() {
        return program_name;
    }
}
