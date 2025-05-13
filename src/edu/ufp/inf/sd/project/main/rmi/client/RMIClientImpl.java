package edu.ufp.inf.sd.project.main.rmi.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIClientImpl extends UnicastRemoteObject implements RMIClientRI {
    public RMIClientImpl() throws RemoteException {
        super();
    }

    @Override
    public void update(String message) throws RemoteException {
        System.out.println(" [RMI Notification] Message received from server: " + message);
    }
}
