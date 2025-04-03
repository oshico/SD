/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ufp.inf.sd.rabbitmqservices.p02_workqueues.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import edu.ufp.inf.sd.rabbitmqservices.util.RabbitUtils;

/**
 * Example from RabbitMQ site:
 * https://www.rabbitmq.com/tutorials/tutorial-two-java.html
 * <p>
 * Create a Work Queue (aka: Task Queue) that will be used to distribute
 * time-consuming tasks among multiple workers. Task Queues avoid doing a
 * resource-intensive task immediately and wait for it to complete. Instead
 * we schedule the task to be done later.
 * Encapsulate a task as a message and send it to a queue. A worker process
 * running in background will pop the tasks and eventually execute the job.
 * When running many workers, tasks will be shared between them.
 * This concept is especially useful in web apps where it is impossible to
 * handle a complex task during a short HTTP request time-window.
 * <p>
 * We could send strings that stand for complex tasks (e.g. images to be resized
 * or pdf files to be rendered). Instead we fake tasks with Thread.sleep(1000);
 * for every dot on the message string, e.g., a fake task "T1..." will take 3 seconds.
 *
 * <p>
 * Running the producer NewTask (each '.' takes 1s on worker):
 * $ runproducer dummy task1...
 *
 * @author rui
 */
public class NewTask {

    // Name of the queue
    //public final static String TASK_QUEUE_NAME="dummy_queue";
    //public final static String LOG_QUEUE_NAME="log_queue";
    //public final static String EMAIL_QUEUE_NAME="email_queue";

    /**
     * Run producer with queue type (cf. dummy / log / email) followed by msg strings (each dummy '.' will sleep 1s):
     * $ runproducer dummy task_msg1. task_msg2..
     *
     * <p>
     * Challenge (run other workers):
     * 1. Run a Worker for logging (appending messages to a log file)
     * $ runconsumer log task_msg1 task_msg2
     * <p>
     * 2. Run a Worker for messaging (sending email);
     * $ runconsumer email task_msg1 task_msg2
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        RabbitUtils.printArgs(args);

        if (args.length<5){
            System.out.println(NewTask.class.getName() + "->main(): must have 5+ args...");
            System.out.println(NewTask.class.getName() + "->main(): run producer with queue type (cf. dummy / log / email) + messages (each '.' will sleep 1s)");
            System.out.println(NewTask.class.getName() + "->main(): e.g. runproducer dummy task1. task2..");
            System.exit(0);
        }
        //Read args passed via shell command
        String host=args[0];
        int port=Integer.parseInt(args[1]);
        String queueType=args[2];
        String queueName=queueType + "_queue";
        //Receive messages to send from argv[3] upwards
        String message=RabbitUtils.getMessage(args, 3);

        //Try-with-resources: connect to broker and get channel
        try (Connection connection=RabbitUtils.newConnection2Server(host, port, "guest", "guest");
             Channel channel=RabbitUtils.createChannel2Server(connection)) {

            System.out.println(NewTask.class.getName() + "->main(): [*] Declaring queues");
            /* We must declare a queue to send to (it is idempotent, i.e. will only be created if it doesn't exist);
               then we can publish a message to the queue;
               The message content is a byte array (can encode whatever we need).
               The previous queue is not Durable/Persistent */
            //channel.queueDeclare(queueName, false, false, false, null);

            /* Declare a queue as Durable (queue will not be lost even if RabbitMQ restarts);
               RabbitMQ does not allow redefine an existing queue with different parameters (hence create a new one) */
            boolean durable=true;
            channel.queueDeclare(queueName, durable, false, false, null);

            System.out.println(NewTask.class.getName() + "->main(): [*] Message to send is '" + message + "'");

            /* To avoid loosing queues when rabbitmq crashes, mark messages as persistent: PERSISTENT_TEXT_PLAIN. */
            //channel.basicPublish("", queueName, null, message.getBytes("UTF-8"));
            channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());

            System.out.println(NewTask.class.getName() + "->main(): [x] Sent '" + message + "'");
        }
        /* finally {
            // Close the channel and the connection... not necessary with try-with-resources
            if (channel!=null) channel.close();
            if (con!=null) con.close();
        }
        */
    }
}
