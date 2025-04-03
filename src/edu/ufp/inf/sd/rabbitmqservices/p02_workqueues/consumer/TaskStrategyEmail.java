package edu.ufp.inf.sd.rabbitmqservices.p02_workqueues.consumer;

import java.util.Date;

public class TaskStrategyEmail implements WorkerStrategyI {

    @Override
    public void execTask(String task) throws Exception {
        doWorkEmail(task);
    }

    /**
     * Do emailing...
     */
    private void doWorkEmail(String task) {
        System.out.println(TaskStrategyEmail.class.getName() + "->main(): [x] doWorkEmail()...");
        Date d=new Date(System.currentTimeMillis());
        String subject="Subject: " + d.toString();
        String msgBody="Msg Body: " + task;
        SendMailHelper.sendMail(SMTPConfigs.MAIL_TO_ADDR, SMTPConfigs.MAIL_FROM_ADDR, SMTPConfigs.SMTP_HOST_ADDR, SMTPConfigs.SMTP_HOST_PORT, "true", SMTPConfigs.SMTP_USER, SMTPConfigs.SMTP_PASS, subject, msgBody);
        System.out.println();
    }
}
