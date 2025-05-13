package edu.ufp.inf.sd.project.main.rabbitmq.consumer;

import com.rabbitmq.client.*;

import edu.ufp.inf.sd.project.main.rmi.server.RMIServerRI;

import java.rmi.Naming;
import java.rmi.RemoteException;

public class ConsumerApp {
    private static final String QUEUE_NAME = "logs";

    public static void main(String[] argv) throws Exception {
        // ===== RMI Setup =====
        String registryIP = "localhost";
        String registryPort = "1099";
        String serviceName = "rmiService";
        String serviceUrl = "rmi://" + registryIP + ":" + registryPort + "/" + serviceName;

        // Lookup RMI server
        RMIServerRI rmiServer = (RMIServerRI) Naming.lookup(serviceUrl);
        System.out.println(" [x] Connected to RMI server at: " + serviceUrl);

        // ===== RabbitMQ Setup =====
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received from RabbitMQ: '" + message + "'");

                // Notify RMI observers
                try {
                    rmiServer.notifyAllObservers(message);
                } catch (RemoteException e) {
                    System.err.println(" [!] RMI notify failed: " + e.getMessage());
                }
            };

            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
            });
        }
    }
}
