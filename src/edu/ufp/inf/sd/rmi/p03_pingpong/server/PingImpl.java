package edu.ufp.inf.sd.rmi.p03_pingpong.server;

import edu.ufp.inf.sd.rmi.p03_pingpong.client.PongRI;
import edu.ufp.inf.sd.rmi.util.threading.ThreadPool;

import static java.lang.Thread.sleep;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * Title: Projecto SD</p>
 * <p>
 * Description: Projecto apoio aulas SD</p>
 * <p>
 * Copyright: Copyright (c) 2005</p>
 * <p>
 * Company: UFP </p>
 *
 * @author Rui S. Moreira
 * @version 1.0
 */
public class PingImpl extends UnicastRemoteObject implements PingRI {

    private final ThreadPool pool;

    // Uses RMI-default sockets-based transport
    // Runs forever (do not passivates) - Do not needs rmid (activation deamon)
    // Constructor must throw RemoteException due to export()
    public PingImpl() throws RemoteException {
        // Invokes UnicastRemoteObject constructor which exports remote object
        super();
        pool = new ThreadPool(10);
    }

    //ping() uses a thread to terminate/return the client call (pong) otherwise,
    //since ping callsback pong again, it would cause an infinit loop call
    //(OutOfMemoryException - stackoverflow).
    @Override
    public void ping(PongRI clientPongRI, Ball b) throws RemoteException {
        System.out.println("\n");
        Date d = new Date(System.currentTimeMillis());
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ping(): will reply to client player " + b.getPlayerID() + "->pong at " + d.toString());

        //Option 1: just reply to pong() - causes outofmemory
        //clientPongRI.pong(b);

        //Option 2: create a thread for each call
        //(new PingThread(clientPongRI, b)).start();

        //Option 3: use a thread pool to execute the runnable
        (new Thread(new PingRunnable(clientPongRI, b))).start();
        this.pool.execute(new PingRunnable(clientPongRI, b));

        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ping(): end!");
    }

    protected static void runReplyPong(PongRI pongRI, Ball b) {
        Thread t = Thread.currentThread();
        try {
            // Create Random generator object
            Random generator = new Random();
            // Slowdown reply with a sleep time
            //long millisecs = Math.abs(generator.nextLong());
            //another way...
            long millisecs = (long) (Math.random() * 2000);
            Logger.getLogger(t.getName()).log(Level.INFO, " waiting " + millisecs + " millisec...");
            sleep(millisecs);
            // Generate a probability of error between 1..100
            int playError = (100 - Math.abs(generator.nextInt(99) + 1));
            if (playError <= 5) {
                //Below 5 there is no reply error and CONTINUES playing
                Logger.getLogger(t.getName()).log(Level.INFO, " reply ball " + b.getPlayerID());
                pongRI.pong(b);
            } else {
                //Above 5 there is a reply error and STOPS playing
                Logger.getLogger(t.getName()).log(Level.INFO, " dropped ball " + b.getPlayerID());
            }
            Logger.getLogger(t.getName()).log(Level.INFO, " server thread end " + b.getPlayerID());

        } catch (InterruptedException | RemoteException ie) {
            Logger.getLogger(t.getName()).log(Level.SEVERE, null, ie);
        }
    }
}