package edu.ufp.inf.sd.rmi.p03_pingpong.server;

import edu.ufp.inf.sd.rmi.p03_pingpong.client.PongRI;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Rui S. Moreira
 * @version 1.0
 *
 * Implements a Thread for playing with each Client
 */
class PingThread extends Thread {

    private PongRI pongRI;
    private Ball ball;

    public PingThread(PongRI pongRI, Ball b) {
        super();
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " received ball from player = " + b.getPlayerID());
        this.pongRI = pongRI;
        this.ball = b;
    }

    @Override
    public void run() {
        PingImpl.runReplyPong(pongRI, ball);
    }
}
