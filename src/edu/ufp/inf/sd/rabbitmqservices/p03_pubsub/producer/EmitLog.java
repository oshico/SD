package edu.ufp.inf.sd.rabbitmqservices.p03_pubsub.producer;


import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import edu.ufp.inf.sd.rabbitmqservices.util.RabbitUtils;


public class EmitLog {

    //public static final String EXCHANGE_NAME="logs_exchange";

    /**
     * Run this app with:
     * $ ./runproducer <msg1> <msg2>...
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

        //Try-with-resources
        try (Connection connection=RabbitUtils.newConnection2Server(host, port,"guest", "guest");
             Channel channel=RabbitUtils.createChannel2Server(connection)) {

            System.out.println(" [x] Declare exchange: '" + exchangeName + "' of type " + BuiltinExchangeType.FANOUT.toString());
            /* Set the Exchange type to FANOUT (multicast to all queues). */
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);

            String message=RabbitUtils.getMessage(args, 3);
          
            /* Publish messages to the logs_exchange instead of the nameless one.
               Fanout exchanges will ignore routingKey (hence set routingKey="").
               Messages will be lost if no queue is bound to the exchange yet. */
            String routingKey="";
            channel.basicPublish(exchangeName, routingKey, null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent: '" + message + "'");
        }
    }
}