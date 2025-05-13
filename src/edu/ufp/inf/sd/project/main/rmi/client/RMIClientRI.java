package edu.ufp.inf.sd.project.main.rmi.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIClientRI extends Remote {
    void update(String message) throws RemoteException;
}
