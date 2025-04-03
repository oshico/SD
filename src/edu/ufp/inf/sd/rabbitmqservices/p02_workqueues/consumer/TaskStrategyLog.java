package edu.ufp.inf.sd.rabbitmqservices.p02_workqueues.consumer;

import java.util.Date;

public class TaskStrategyLog implements WorkerStrategyI {
    @Override
    public void execTask(String task) throws Exception {
        doWorkLogging(task);
    }

    /**
     * Do logging...
     */
    private void doWorkLogging(String task) {
        System.out.println(TaskStrategyLog.class.getName() + "->main(): [x] doWorkLogging()...");
        Date d=new Date(System.currentTimeMillis());
        LogSingletonHelper singletonLog=LogSingletonHelper.createSingletonLog(LogSingletonHelper.PATH_TO_LOG_FILE);
        singletonLog.appendToLog("[" + d.toString() + "] Worker appended another line task: " + task);
        System.out.println();
    }
}
