package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.client.ObserverRI;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SubjectFileSystemImpl extends UnicastRemoteObject implements SubjectFileSystemRI {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String username;
    private final Database database;
    private final ArrayList<ObserverRI> observers = new ArrayList<>();

    public SubjectFileSystemImpl(String username, Database database) throws RemoteException {
        super();
        this.username = username;
        this.database = database;
    }

    @Override
    public ArrayList setStateFileSystemOperation(StateFileSystemOperation stateFileSystemOperation) throws RemoteException {
        return null;
    }

    @Override
    public void attachObserver(ObserverRI observer) throws RemoteException {
        this.observers.add(observer);
    }

    @Override
    public void detachObserver(ObserverRI observer) throws RemoteException {
        this.observers.add(observer);
    }

    @Override
    public void notifyObservers(StateFileSystemOperation stateFileSystemOperation) throws RemoteException {
        for (ObserverRI observer : observers) {
            try {
                observer.update(stateFileSystemOperation);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
