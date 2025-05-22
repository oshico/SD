package edu.ufp.inf.sd.project.client;


import edu.ufp.inf.sd.project.server.StateFileSystemOperation;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ObserverImpl extends UnicastRemoteObject implements ObserverRI {
    private final ClientMain client;

    public ObserverImpl(ClientMain client) throws RemoteException {
        super();
        this.client = client;
    }

    @Override
    public void update(StateFileSystemOperation stateFileSystemOperation) throws RemoteException {
        StateFileSystemOperation.executeOperation(stateFileSystemOperation);
    }
}
