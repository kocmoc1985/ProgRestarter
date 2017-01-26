/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Logger;

/**
 * For proper use of this class, each method of the class that inherits this class should have a "String call" parameter
 * and you should start each method by calling "loggEvent" method. Places where Exceptions are thrown could start by
 * calling the "loggException" method
 * @author Administrator
 */
public class FileLogger {

    public String className;

    public FileLogger(String actualClassName) {
        this.className = actualClassName;
    }

    /**
     * For tracing of method visiting
     * @param iam_in
     * @param called_from
     */
    public void loggEvent(String iam_in, String called_from) {
        LoggR loggR = new LoggR(iam_in, called_from, className);
        loggR = null;
    }

    /**
     * For tracing of method visiting
     * @param iam_in
     * @param called_from
     */
    public void loggEventUserMessage(String iam_in, String value_to_trace,String user_msg) {
        LoggR loggR = new LoggR(iam_in, value_to_trace, className, user_msg);
        loggR = null;
    }

    /**
     * For tracing of thrown Exceptions
     * @param ex
     */
    public void loggException(Exception ex, String className, int line) {
        LoggR loggR = new LoggR(ex, className, line);
        loggR = null;
    }

    /**
     * For tracing of thrown Exceptions
     * @param ex
     */
    public void loggException(Throwable ex,String className, int line) {
        LoggR loggR = new LoggR(ex,className, line);
        loggR = null;
    }



    /**
     * For tracing of thrown Exceptions
     * @param ex
     */
    public void loggException(Exception ex, String className) {
        LoggR loggR = new LoggR(ex, className);
        loggR = null;
    }

    /**
     * For tracing of thrown Exceptions
     * @param ex
     */
    public void loggException(Exception ex) {
        LoggR loggR = new LoggR(ex);
        loggR = null;
    }

    public int getLineNumber() {
        return Thread.currentThread().getStackTrace()[2].getLineNumber();
    }

    public static String getMethodName(final int depth) {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        return ste[ste.length - 1 - depth].getMethodName();
    }
}
