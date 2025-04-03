package edu.ufp.inf.sd.rabbitmqservices.p02_workqueues.consumer;

public class TaskStrategyDummy implements WorkerStrategyI {

    @Override
    public void execTask(String task)throws Exception {
        doDummyWork(task);
    }

    /**
     * Fake a second of work for every '.' in the task message
     */
    private void doDummyWork(String task) throws InterruptedException {
        System.out.print(TaskStrategyDummy.class.getName() + "->main(): [x] doDummyWork()");
        for (char ch : task.toCharArray()) {
            if (ch == '.') {
                Thread.sleep(1000);
                System.out.print(".");
            }
        }
        System.out.println();
    }
}
