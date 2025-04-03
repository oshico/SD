package edu.ufp.inf.sd.rabbitmqservices.p02_workqueues.consumer;

import com.sun.mail.smtp.SMTPTransport;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.Properties;

/**
 * A simple Java Application to send emails via SMTP:
 * <p>
 * Manage dependencies:
 * - install JavaMail API <https://javaee.github.io/javamail/>
 *   (copy javax.mail-x.y.z.jar to *lib* folder)
 * OR
 * - use Maven/Graddle for managing dependencies
 * <p>
 * Use an email account from an smtp server:
 * - create free account on <https://mailtrap.io/>
 */
public class SendMailHelper {

    /**
     * Testing method...
     * @param args
     */
    public static void main(String[] args) {
        sendMail(SMTPConfigs.MAIL_TO_ADDR, SMTPConfigs.MAIL_FROM_ADDR, SMTPConfigs.SMTP_HOST_ADDR, SMTPConfigs.SMTP_HOST_PORT, "true", SMTPConfigs.SMTP_USER, SMTPConfigs.SMTP_PASS, "Subject test", "Msg Body test");
        //sendMailWithAttach(SMTPConfigs.MAIL_TO_ADDR, SMTPConfigs.MAIL_FROM_ADDR, SMTPConfigs.SMTP_HOST_ADDR, SMTPConfigs.SMTP_HOST_PORT, "true", SMTPConfigs.SMTP_USER, SMTPConfigs.SMTP_PASS, "Subject test", "Msg Body test", SMTPConfigs.SMTP_ATTACH_FILE);
    }

    /**
     * Send SMTP email message
     *
     * @param to
     * @param from
     * @param host
     * @param port
     * @param auth
     * @param user
     * @param pass
     * @param subject
     * @param bodyMsg
     */
    public static void sendMail(String to, String from, String host, String port, String auth, String user, String pass, String subject, String bodyMsg) {
        // Get system properties
        Properties properties=System.getProperties();

        // Setup mail server
        properties.setProperty(SMTPConfigs.KEY_MAIL_SMTP_HOST, host);
        properties.put(SMTPConfigs.KEY_MAIL_SMTP_PORT, port);
        properties.put(SMTPConfigs.KEY_MAIL_SMTP_AUTH, auth);
        properties.setProperty(SMTPConfigs.KEY_MAIL_USER, user);
        properties.setProperty(SMTPConfigs.KEY_MAIL_PASSWORD, pass);

        // Get default Session object
        Session session=Session.getDefaultInstance(properties);

        SMTPTransport t=null;

        try {
            // Create default MimeMessage object
            MimeMessage message=new MimeMessage(session);

            // Set header fields
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(bodyMsg);
            message.setSentDate(new Date());

            // Send message...
            // Get SMTPTransport
            t=(SMTPTransport) session.getTransport("smtp");
            // Connect
            t.connect(host, user, pass);
            // Send
            t.sendMessage(message, message.getAllRecipients());
            // Print server response
            System.out.println("Response from SMTP server: " + t.getLastServerResponse());

        } catch (MessagingException mex) {
            mex.printStackTrace();
        } finally {
            if (t != null) {
                try {
                    t.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     *
     * @param to
     * @param from
     * @param host
     * @param port
     * @param auth
     * @param user
     * @param pass
     * @param subject
     * @param bodyMsg
     * @param attachFilePath
     */
    public static void sendMailWithAttach(String to, String from, String host, String port, String auth, String user, String pass, String subject, String bodyMsg, String attachFilePath) {
        // Get system properties
        Properties properties=System.getProperties();
        properties.put("mail.smtp.auth", "true");

        Session session=Session.getInstance(properties, null);
        Message msg=new MimeMessage(session);

        SMTPTransport t = null;

        try {
            // Set header properties
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            msg.setSubject(subject);

            // Body text part
            MimeBodyPart mimeBodyPart1=new MimeBodyPart();
            mimeBodyPart1.setText(bodyMsg);

            // Attach file part
            MimeBodyPart mimeBodyPart2=new MimeBodyPart();
            FileDataSource fds=new FileDataSource(attachFilePath);
            mimeBodyPart2.setDataHandler(new DataHandler(fds));
            mimeBodyPart2.setFileName(fds.getName());

            Multipart mp=new MimeMultipart();
            mp.addBodyPart(mimeBodyPart1);
            mp.addBodyPart(mimeBodyPart2);

            msg.setContent(mp);

            t=(SMTPTransport) session.getTransport("smtp");

            // connect
            t.connect(host, user, pass);

            // send
            t.sendMessage(msg, msg.getAllRecipients());

            System.out.println("Response: " + t.getLastServerResponse());
        } catch (MessagingException e) {
            e.printStackTrace();
        } finally {
            if (t!=null) {
                try {
                    t.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
