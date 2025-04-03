package edu.ufp.inf.sd.rabbitmqservices.p02_workqueues.consumer;

public interface WorkerStrategyI {
    public void execTask(String task) throws Exception;
}
