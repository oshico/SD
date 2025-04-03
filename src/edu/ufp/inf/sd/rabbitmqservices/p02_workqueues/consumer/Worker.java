package edu.ufp.inf.sd.rabbitmqservices.p02_workqueues.consumer;

import com.rabbitmq.client.*;
import edu.ufp.inf.sd.rabbitmqservices.p02_workqueues.producer.NewTask;
import edu.ufp.inf.sd.rabbitmqservices.util.RabbitUtils;


/**
 * Round-robin dispatching:
 * One of the advantages of using a Task Queue is the ability to easily parallelize work.
 * If we are building up a backlog of work, we can just add workers and scale easily.
 *
 * <p>
 * Run 2 worker instances at the same time (one on each shell).
 * They will both get messages from the queue, since, by default, RabbitMQ will
 * send each message to next client (in sequence).
 * On average every client will get the same number of messages.
 * This way of distributing messages is called round-robin (try this with 3+ workers).
 *
 * <p>
 * Message acknowledgment:
 * With current channel queue, once RabbitMQ delivers a message to the customer it
 * immediately marks it for deletion. In this case, if you kill a worker we
 * will lose the message it was just processing. We also lose all the messages
 * that were dispatched to this particular worker but were not yet handled.
 *
 * <p>
 * To not lose any tasks (in case worker dies) and deliver them to another worker,
 * RabbitMQ supports message acknowledgments, i.e., an ack is sent back by the client
 * to tell RabbitMQ that a particular message has been received, processed and
 * that RabbitMQ is free to delete it.
 *
 * <p>
 * If a client dies (i.e., channel is closed, connection is closed, or
 * TCP connection is lost) without sending an ack, RabbitMQ will understand
 * that a message was not processed fully and will re-queue it.
 * If there are other consumers online at the same time, it will then quickly
 * re-deliver it to another client. That way you can be sure that no message
 * is lost, even if the workers occasionally die.
 * There are no message timeouts and RabbitMQ will re-deliver the message when
 * the client dies. It is fine even if processing a message takes a long time.
 * "Manual message acknowledgments" are turned on by default (we may explicitly
 * turn them off via the autoAck=true flag).
 *
 * <p>
 * Forgotten acknowledgment:
 * To debug lack of ack use rabbitmqctl to print the messages_unacknowledged field:
 * - Linux/Mac:
 *  $ sudo rabbitmqctl list_queues name messages_ready messages_unacknowledged
 * - Win:
 *  > rabbitmqctl.bat list_queues name messages_ready messages_unacknowledged
 *
 * <p>
 * Message durability:
 * Messages/Tasks will be lost if RabbitMQ server stops, because when RabbitMQ
 * quits or crashes it will forget the queues and messages unless you tell it not to.
 *
 * <p>
 * Two things are required to make sure that messages are not lost, i.e.,
 * mark both the queue and messages as durable:
 * 1) declare the queue as *durable* (so RabbitMQ will never lose the queue);
 * 2) mark messages as persistent by setting MessageProperties.PERSISTENT_TEXT_PLAIN.
 *
 * <p>
 * NB: persistence guarantees ARE NOT strong, i.e., may be cached and
 * not immediately saved/persisted.
 *
 * <p>
 * Fair dispatch:
 * RabbitMQ dispatches a message when it enters the queue.
 * It does not look at the number of unacknowledged messages for a client.
 * It just blindly dispatches every n-th message to the n-th client.
 * Hence, a worker could get all heavy tasks while another the light ones.
 *
 * <p>
 * To guarantee fairness use basicQos() method for setting prefetchCount = 1.
 * This tells RabbitMQ not to give more than one message to a worker at a time,
 * i.e. do not dispatch new message to a worker until it has not processed and
 * acknowledged the previous one. Instead, dispatch it to the next worker
 * that is not still busy.
 *
 * <p>
 * Running the worker:
 * 0. Run a Worker for dummy work: thread that sleeps 1s for each '.' received in message;
 *      $ runconsumer dummy
 *
 * <p>
 * Challenge (create other workers):
 * 1. Run a Worker for logging: appending the message to a log file;
 *      $ runconsumer log
 *
 * <p>
 * 2. Run a Worker for messageing: sending an email using the javamail API (see SendMailHelper class);
 *      $ runconsumer email
 *
 * @author rui
 */
public class Worker {

    /**
     * Constante used to compare received task type.
     */
    public static final String WORKER_DUMMY_TYPE="dummy";

    /**
     * Strategy reference to object implementing specific task type.
     */
    private WorkerStrategyI strategy;

    /**
     * Constructor receives strategy object to execute (cf. TaskStrategyDummy, TaskStrategyLog, TaskStrategyEmail).
     *
     * @param strategy
     */
    public Worker(WorkerStrategyI strategy) {
        this.strategy=strategy;
    }

    /**
     * Executes the received strategy object.
     *
     * @param task
     * @throws Exception
     */
    public void executeStrategy(String task) throws Exception {
        strategy.execTask(task);
    }

    /**
     * Run consumer by specifying queue type (cf. dummy, log, email) to execute:
     *  $ ./runconsumer dummy
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        RabbitUtils.printArgs(args);

        if (args.length<3){
            System.out.println(Worker.class.getName() + "->main(): must have 3 args...");
            System.out.println(Worker.class.getName() + "->main(): run Worker for dummy queue (work thread sleeps 1s for each '.' received in message)");
            System.out.println(Worker.class.getName() + "->main(): e.g. runconsumer dummy");
            System.exit(0);
        }

        //Read args passed via shell command
        String host=args[0];
        int port=Integer.parseInt(args[1]);
        //Get the worker type: dummy, log or email
        String workerType=args[2];
        System.out.println(Worker.class.getName() + "->main(): [*] workerType " + workerType);

        String queueName = workerType+"_queue";
        Worker worker=null;

        //Create a given task strategy for the worker to execute (Strategy design pattern)
        switch (workerType) {
            case WORKER_DUMMY_TYPE:
                worker=new Worker(new TaskStrategyDummy());
                worker.run(host, port, queueName);
                break;
            default:
                System.out.println(Worker.class.getName() + "->main(): [*] workerType " + workerType + " DOES NOT exist!!");
        }
    }

    /**
     * Run the
     * @param taskQueueName
     * @throws Exception
     */
    public void run(String host, int port, String taskQueueName) throws Exception {
        /* Open a connection and a channel, and declare the queue to which to consume.
         Declare the queue here, as well, because we might start the client before the publisher. */
        Connection connection=RabbitUtils.newConnection2Server(host, port, "guest", "guest");
        Channel channel=RabbitUtils.createChannel2Server(connection);

        System.out.println(Worker.class.getName() + "->main(): [*] Going to declare queue " + taskQueueName);

        /* Declare a queue as Durable (queue will not be lost even if RabbitMQ restarts);
        NB: RabbitMQ does not allow to redefine existing queue with different parameters (create a new one) */
        boolean durable=true;
        channel.queueDeclare(taskQueueName, durable, false, false, null);
        System.out.println(Worker.class.getName() + "->main(): [*] Waiting for messages (to exit press CTRL+C)");

        /* The server pushes messages asynchronously; the callback will buffer messages until ready to use them. */
        //Set basicQoS(1): does not dispatch new message to worker until it has sent ack;
        //Instead, dispatch message to the next worker that is not still busy.
        int prefetchCount=1;
        channel.basicQos(prefetchCount);

        //Create consumer which will doDummyWork()...
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(Thread.currentThread().getName() + "->deliverCallback(): [x] Tag: [" + consumerTag + "] Received '" + message + "'");
            try {
                //Execute the worker task strategy
                this.executeStrategy(message);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println(Thread.currentThread().getName() + "->deliverCallback(): [x] Tag: [" + consumerTag + "] Done processing task");
                //The Worker MUST Manually ACK each finalised task (for it to be removed from queue).
                //Hence, even if worker is killed (CTRL+C) while processing a message, nothing will be lost.
                //Soon after the worker dies all unacknowledged messages will be redelivered to other workers.
                //Ack must be sent on same channel msg was received, otherwise raises channel-level-protocol exception.
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };

        //boolean autoAck = true; //When true disables "Manual message acks"
        boolean autoAck=false; //When false worker must sends ack (once it is done with a task).
        //Register handler deliverCallback with autoAck=false
        channel.basicConsume(taskQueueName, autoAck, deliverCallback, consumerTag -> {
            System.out.println(Thread.currentThread().getName() + "->consumerCallback(): [x] Tag: [" + consumerTag + "] consumed!");
        });

        //DO NOT close otherwise it will terminate consumer thread
        //channel.close();
        //connection.close();
    }
}
