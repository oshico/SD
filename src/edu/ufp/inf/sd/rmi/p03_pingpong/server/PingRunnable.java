package edu.ufp.inf.sd.rmi.p03_pingpong.server;

import edu.ufp.inf.sd.rmi.p03_pingpong.client.PongRI;

import java.util.logging.Level;
import java.util.logging.Logger;

class PingRunnable implements Runnable {

    private PongRI pongRI;
    private Ball ball;

    PingRunnable(PongRI pongRI, Ball b) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " received ball from player = " + b.getPlayerID());
        this.pongRI = pongRI;
        this.ball = b;
    }

    @Override
    public void run() {
        PingImpl.runReplyPong(pongRI, ball);
    }
}
