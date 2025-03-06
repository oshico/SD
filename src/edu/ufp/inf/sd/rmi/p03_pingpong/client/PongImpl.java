package edu.ufp.inf.sd.rmi.p03_pingpong.client;

import edu.ufp.inf.sd.rmi.p03_pingpong.server.Ball;
import edu.ufp.inf.sd.rmi.p03_pingpong.server.PingRI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PongImpl extends UnicastRemoteObject implements PongRI {

    PingRI pingRI;
    Ball b;

    public PongImpl(PingRI pingRI, Ball b) throws RemoteException {
        this.pingRI = pingRI;
        this.b = b;
    }

    @Override
    public void pong(Ball b) throws RemoteException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "got ball from server {0}", b);

        this.pingRI.ping(this, this.b);
    }


    public void startPlaying() {
        try {
            this.pingRI.ping(this, this.b);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
