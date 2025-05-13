package edu.ufp.inf.sd.project.main.rabbitmq.producer;

import com.rabbitmq.client.*;
import edu.ufp.inf.sd.project.util.RabbitUtils;

public class ProducerApp {
    private static final String QUEUE_NAME = "logs";

    public static void main(String[] args) throws Exception {
        // Example message from args or default
        String message = (args.length > 0) ? String.join(" ", args) : "Hello from ProducerApp!";

        // Setup connection and channel via RabbitUtils
        Connection connection = RabbitUtils.newConnection2Server("localhost", 5672, "guest", "guest");
        Channel channel = RabbitUtils.createChannel2Server(connection);

        // Declare the queue (if not already)
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // Send the message
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent: '" + message + "'");

        // Clean up
        channel.close();
        connection.close();
    }
}
