package edu.ufp.inf.sd.project.client;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ObserverImpl extends UnicastRemoteObject implements ObserverRI {
    private final ClientMain client;

    public ObserverImpl(ClientMain client) throws RemoteException {
        super();
        this.client = client;
    }
}
