package edu.ufp.inf.sd.project.main.rmi.server;

import edu.ufp.inf.sd.project.main.rmi.client.RMIClientRI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServerRI extends Remote {
    void registerObserver(RMIClientRI observer) throws RemoteException;

    void notifyAllObservers(String message) throws RemoteException;
}
