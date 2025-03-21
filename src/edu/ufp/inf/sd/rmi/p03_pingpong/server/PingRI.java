package edu.ufp.inf.sd.rmi.p03_pingpong.server;

import edu.ufp.inf.sd.rmi.p03_pingpong.client.PongRI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PingRI extends Remote {
    public void ping(PongRI clientPongRI, Ball b) throws RemoteException;
}
