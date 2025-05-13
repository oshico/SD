package edu.ufp.inf.sd.project.main.rmi.server;

import edu.ufp.inf.sd.project.main.rmi.client.RMIClientRI;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class RMIServerImpl extends UnicastRemoteObject implements RMIServerRI {
    private final List<RMIClientRI> observers;

    public RMIServerImpl() throws RemoteException {
        super();
        this.observers = new ArrayList<>();
    }

    @Override
    public synchronized void registerObserver(RMIClientRI observer) throws RemoteException {
        if (!observers.contains(observer)) {
            observers.add(observer);
            System.out.println("Client registered: " + observer);
        }
    }

    @Override
    public synchronized void notifyAllObservers(String message) throws RemoteException {
        System.out.println("Notifying all clients: " + message);
        for (RMIClientRI client : observers) {
            client.update(message);
        }
    }
}
