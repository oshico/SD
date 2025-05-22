package edu.ufp.inf.sd.project.client;

import edu.ufp.inf.sd.project.server.StateFileSystemOperation;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ObserverRI extends Remote {

    void update(StateFileSystemOperation stateFileSystemOperation) throws RemoteException;
}
