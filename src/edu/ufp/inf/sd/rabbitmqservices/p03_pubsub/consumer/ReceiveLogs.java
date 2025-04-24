/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ufp.inf.sd.rabbitmqservices.p03_pubsub.consumer;

import com.rabbitmq.client.*;
import edu.ufp.inf.sd.rabbitmqservices.util.RabbitUtils;

/**
 * Deliver a message to multiple consumers, each on is own queue (pattern known as "publish/subscribe").
 * <p>
 * Build a simple logging system with two programs:
 * - Emitter (producer) sends log messages and Receiver (consumer) receives and prints them.
 * - Every running copy of Receiver will get the messages (broadcasted to all receivers). One receiver may direct logs
 * to disk (logs.log file) while another may print logs on the screen.
 * <p>
 * Exchanges:
 * A server never sends messages directly to a queue, instead send messages to an *exchange*.
 * The exchange receives messages from producers and pushes them to queues, according the exchange type
 * (cf. direct, topic, headers and fanout).
 * <p>
 * Listing exchanges:
 * To list the exchanges on the server you can run rabbitmqctl:
 * sudo rabbitmqctl list_exchanges
 * There will be amq.* exchanges and the default (unnamed or nameless) exchange.
 * <p>
 * Temporary queues:
 * Previously we used queues with name (e.g. hello_queue, task_queue) to point producers and workers to the same queue.
 * Now we want to receive all log messages and the current flowing messages not in the old ones.
 * So we need two things:
 * i) create an empty queue when connecting to rabbitmq;
 * ii) automatically delet the queue once client disconnects.
 * <p>
 * Binding:
 * inform exchange to send messages to a queue (i.e. queue is interested in messages from the exchange).
 * <p>
 * Listing bindings:
 * rabbitmqctl list_bindings
 * <p>
 * Running Receivers:
 * java -cp $CP ReceiveLogs > logs.log
 * java -cp $CP ReceiveLogs
 *
 * @author rui
 */
public class ReceiveLogs {

    /**
     * Run this app with:
     * $ ./runconsumer
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        //Print args
        RabbitUtils.printArgs(args);

        //Read args passed via shell command
        String host=args[0];
        int port=Integer.parseInt(args[1]);
        String exchangeName=args[2];

        Connection connection=RabbitUtils.newConnection2Server(host, port, "guest", "guest");
        Channel channel=RabbitUtils.createChannel2Server(connection);

        /* Use the Exchange FANOUT type: broadcasts all messages to all queues */
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);
    
        /* Create a non-durable, exclusive, autodelete queue with a generated name.
           The string queueName will contains a random queue name (e.g. amq.gen-JzTY20BRgKO-HjmUJj0wLg) */
        String queueName=channel.queueDeclare().getQueue();
    
        /* Create binding: tell exchange to send messages to a queue;
           The fanout exchange ignores last parameter (routing/binding key) */
        String routingKey="";
        channel.queueBind(queueName, exchangeName, routingKey);

        System.out.println(" [*] Waiting for messages... to exit press CTRL+C");

        //Use a DeliverCallback instead of a DefaultConsumer for the Receiver
        DeliverCallback deliverCallback=(consumerTag, delivery) -> {
            String message=new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Consumer Tag [" + consumerTag + "] - Received '" + message + "'");
        };
        CancelCallback cancelCalback=(consumerTag) -> {
            System.out.println(" [x] Consumer Tag [" + consumerTag + "] - Cancel Callback invoked!");
        };
        channel.basicConsume(queueName, true, deliverCallback, cancelCalback);

        //DO NOT close connection neither channel otherwise it will terminate consumer
    }
}
