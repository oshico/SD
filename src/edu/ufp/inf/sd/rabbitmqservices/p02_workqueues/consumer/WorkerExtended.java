/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ufp.inf.sd.rabbitmqservices.p02_workqueues.consumer;


import edu.ufp.inf.sd.rabbitmqservices.util.RabbitUtils;

/**
 * Extend Worker with 2 more working strategies: log and email.
 * <p>
 * Running the worker:
 * 0. Run a Worker for dummy work: thread that sleeps 1s for each '.' received in message;
 * $ runconsumer dummy
 * <p>
 * 1. Run a Worker for logging: appending the message to a log file;
 * $ runconsumer log
 * <p>
 * 2. Run a Worker for messageing: sending an email using the javamail API (see SendMailHelper class);
 * $ runconsumer email
 *
 * @author rui
 */
public class WorkerExtended extends Worker {

    public static final String WORKER_LOG_TYPE="log";
    public static final String WORKER_EMAIL_TYPE="email";

    public WorkerExtended(WorkerStrategyI strategy) {
        super(strategy);
    }

    public static void main(String[] args) throws Exception {
        RabbitUtils.printArgs(args);

        if (args.length<3){
            System.out.println(WorkerExtended.class.getName() + "->main(): must have 3 args...");
            System.out.println(WorkerExtended.class.getName() + "->main(): run Worker for dummy | logging | email queue");
            System.out.println(WorkerExtended.class.getName() + "->main(): e.g. runconsumer dummy | log | email");
            System.exit(0);
        }

        //Read args passed via shell command
        String host=args[0];
        int port=Integer.parseInt(args[1]);
        //Get the worker type: dummy, log or email
        String workerType=args[2];
        System.out.println(WorkerExtended.class.getName() + "->main(): [*] workerType " + workerType);

        String queueName=workerType + "_queue";
        Worker worker=null;

        //Create a given task strategy for the worker to execute (Strategy design pattern)
        switch (workerType) {
            case WORKER_DUMMY_TYPE:
                worker=new Worker(new TaskStrategyDummy());
                worker.run(host, port, queueName);
                break;
            case WORKER_LOG_TYPE:
                worker=new Worker(new TaskStrategyLog());
                worker.run(host, port, queueName);
                break;
            case WORKER_EMAIL_TYPE:
                worker=new Worker(new TaskStrategyEmail());
                worker.run(host, port, queueName);
                break;
            default:
                System.out.println(WorkerExtended.class.getName() + "->main(): [*] workerType " + workerType + " DOES NOT exist!!");
        }
    }
}
