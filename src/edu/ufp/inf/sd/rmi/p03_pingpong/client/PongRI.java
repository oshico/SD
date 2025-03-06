package edu.ufp.inf.sd.rmi.p03_pingpong.client;

import edu.ufp.inf.sd.rmi.p03_pingpong.server.Ball;
import java.rmi.*;

public interface PongRI extends Remote {
    public void pong(Ball b) throws RemoteException;
}
