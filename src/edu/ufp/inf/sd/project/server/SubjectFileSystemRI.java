package edu.ufp.inf.sd.project.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.ufp.inf.sd.project.client.ObserverRI;

public interface SubjectFileSystemRI extends Remote {

    void attachObserver(ObserverRI observer) throws RemoteException;

    void detachObserver(ObserverRI observer) throws RemoteException;

    ArrayList setStateFileSystemOperation(StateFileSystemOperation stateFileSystemOperation) throws RemoteException;
}
