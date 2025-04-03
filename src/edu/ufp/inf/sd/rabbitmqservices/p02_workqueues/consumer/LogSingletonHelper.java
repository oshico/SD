/**
 * <p>Title: Projecto SD</p>
 *
 * <p>Description: Projecto apoio aulas SD</p>
 *
 * <p>Copyright: Copyright (c) 2020</p>
 *
 * <p>Company: UFP & INESC Porto</p>
 *
 * @author Rui Moreira
 * @version 1.0
 */
package edu.ufp.inf.sd.rabbitmqservices.p02_workqueues.consumer;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogSingletonHelper {

    /**
     * Testing method...
     * @param args
     */
    public static void main(String args[]){
        LogSingletonHelper singletonLog = createSingletonLog(PATH_TO_LOG_FILE);
        singletonLog.appendToLog("Another line");
    }


    public final static String PATH_TO_LOG_FILE= "/home/oshico/IdeaProjects/SD/test/log/workerlog.txt";

    private static LogSingletonHelper singletonLog;
    private final File fileLog;

    /** private - Avoid direct instantiation */
    private LogSingletonHelper(String file) {
        fileLog = new File(file);
    }
    
    public synchronized static LogSingletonHelper createSingletonLog(String file){
        if (singletonLog ==null){
            singletonLog= new LogSingletonHelper(file);
        }
        return singletonLog;
    }

    /**
     *
     * @param logEntry
     */
    public synchronized void appendToLog(String logEntry) {
        try (RandomAccessFile raf = new RandomAccessFile(fileLog, "rw")){
            //Send file pointer to end of file
            raf.seek(raf.length());
            // Subsequent write() operations will be appended to file
            // For UTF files use (NewLine = \n):
            //raf.writeUTF("\nYet another line appended to file.");
            // For ANSI byte/char files use (NewLine = \r\n):
            raf.writeBytes("\r\n"+logEntry);
        } catch (IOException ex) {
            Logger.getLogger(LogSingletonHelper.class.getName()).log(Level.SEVERE, ex.toString());
        }
    }
    
}

