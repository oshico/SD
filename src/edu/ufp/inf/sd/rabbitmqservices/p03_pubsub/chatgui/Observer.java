package edu.ufp.inf.sd.rabbitmqservices.p03_pubsub.chatgui;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.*;
import edu.ufp.inf.sd.rabbitmqservices.util.RabbitUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author rui
 */
public class Observer {

    //Reference for gui
    private final ObserverGuiClient gui;
    private final Channel channelToRabbitMq;
    private final String exchangeName;
    private final BuiltinExchangeType exchangeType;
    private final String messageFormat;

    //Store received message to be get by gui
    private String receivedMessage;

    /**
     * @param gui
     */
    public Observer(ObserverGuiClient gui, String host, int port, String user, String pass, String exchangeName, BuiltinExchangeType exchangeType, String messageFormat) throws IOException, TimeoutException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " going to attach observer to host: " + host + "...");

        // Set GUI
        this.gui = gui;
        // Set exchange preferences
        this.exchangeName = exchangeName;
        this.exchangeType = exchangeType;
        this.messageFormat = messageFormat;

        Connection connection = RabbitUtils.newConnection2Server(host, port, "guest", "guest");
        this.channelToRabbitMq = RabbitUtils.createChannel2Server(connection);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Connection established to RabbitMQ on host: " + host);

        // Bind exchange to channel
        bindExchangeToChannelRabbitMQ();

        // Attach consumer to channel exchange
        attachConsumerToChannelExchangeWithKey();
    }

    /**
     * Binds the channel to given exchange name and type.
     */
    private void bindExchangeToChannelRabbitMQ() throws IOException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Declaring Exchange '" + this.exchangeName + "' with type " + this.exchangeType);
        // Declare the exchange with the type specified (Fanout in this case)
        this.channelToRabbitMq.exchangeDeclare(this.exchangeName, this.exchangeType);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Exchange bound to channel.");
    }

    /**
     * Creates a Consumer associated with an unnamed queue.
     */
    public void attachConsumerToChannelExchangeWithKey() throws IOException {
        // Create a non-durable, exclusive, autodelete queue with a random generated name
        String queueName = this.channelToRabbitMq.queueDeclare().getQueue();

        // Bind the queue to the exchange (Fanout exchange ignores the binding key)
        String bindingKey = ""; // Binding key is ignored by Fanout exchange
        this.channelToRabbitMq.queueBind(queueName, this.exchangeName, bindingKey);

        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Created consumerChannel bound to Exchange " + this.exchangeName + "...");

        // Use DeliverCallback lambda function to receive messages from the queue
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), messageFormat);
            // Store the received message
            this.setReceivedMessage(message);
            System.out.println(" [x] Consumer Tag [" + consumerTag + "] - Received '" + message + "'");
            // Notify the GUI about the new message arrival
            this.gui.updateTextArea();
        };

        CancelCallback cancelCallback = consumerTag -> {
            System.out.println(" [x] Consumer Tag [" + consumerTag + "] - Cancel Callback invoked!");
        };

        // Start consuming messages from the queue
        this.channelToRabbitMq.basicConsume(queueName, true, deliverCallback, cancelCallback);
    }

    /**
     * Publish messages to existing exchange instead of the nameless one.
     * - The routingKey is empty ("") since the fanout exchange ignores it.
     * - Messages will be lost if no queue is bound to the exchange yet.
     * - Set Basic properties: MessageProperties.PERSISTENT_TEXT_PLAIN, etc.
     */
    public void sendMessage(String msgToSend) throws IOException {
        // RoutingKey will be ignored by FANOUT exchange
        String routingKey = "";
        BasicProperties prop = MessageProperties.PERSISTENT_TEXT_PLAIN;
        // Publish message into exchange
        this.channelToRabbitMq.basicPublish(this.exchangeName, routingKey, prop, msgToSend.getBytes());
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Message sent to exchange '" + this.exchangeName + "': " + msgToSend);
    }

    /**
     * @return the most recent message received from the broker
     */
    public String getReceivedMessage() {
        return receivedMessage;
    }

    /**
     * @param receivedMessage the received message to set
     */
    public void setReceivedMessage(String receivedMessage) {
        this.receivedMessage = receivedMessage;
    }
}
