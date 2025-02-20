package edu.ufp.inf.sd.rmi.util.threading;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ThreadPool class for managing a pool of worker threads.
 */
public class ThreadPool {

    private final int poolsize;
    private final PoolThread[] poolAvailableThreads;
    private final LinkedList<Runnable> listRunnableThreads = new LinkedList<>();
    private volatile boolean running = true; // Flag to control execution

    /**
     * Constructor of the thread pool.
     *
     * @param nt - number of threads
     */
    public ThreadPool(int nt) {
        this.poolsize = nt;
        poolAvailableThreads = new PoolThread[this.poolsize];
        startThreadPool();
    }

    /**
     * Starts the pool of threads.
     */
    private void startThreadPool() {
        for (int i = 0; i < this.poolsize; i++) {
            poolAvailableThreads[i] = new PoolThread();
            poolAvailableThreads[i].start();
        }
    }

    /**
     * Adds a Runnable task to the queue and notifies a waiting thread.
     * @param r - Runnable task to be executed
     */
    public void execute(Runnable r) {
        synchronized (listRunnableThreads) {
            listRunnableThreads.addLast(r);
            listRunnableThreads.notify();
        }
    }

    /**
     * Stops the thread pool gracefully.
     */
    public void stop() {
        synchronized (listRunnableThreads) {
            running = false;
            listRunnableThreads.notifyAll(); // Wake up all waiting threads
        }
    }

    /**
     * Worker thread class.
     */
    private class PoolThread extends Thread {
        public void run() {
            while (running) {
                Runnable r;
                synchronized (listRunnableThreads) {
                    while (listRunnableThreads.isEmpty() && running) {
                        try {
                            listRunnableThreads.wait();
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt(); // Restore interrupted state
                            return; // Exit the thread
                        }
                    }

                    if (!running) return; // Check stop flag before proceeding
                    r = listRunnableThreads.removeFirst();
                }

                try {
                    r.run();
                } catch (RuntimeException e) {
                    Logger.getLogger(ThreadPool.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }
}
