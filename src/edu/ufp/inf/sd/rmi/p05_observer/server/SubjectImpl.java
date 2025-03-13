package edu.ufp.inf.sd.rmi.p05_observer.server;

import edu.ufp.inf.sd.rmi.p05_observer.client.ObserverRI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class SubjectImpl extends UnicastRemoteObject implements SubjectRI {
    private State state;
    private final List<ObserverRI> observers;

    public SubjectImpl() throws RemoteException {
        super();
        this.observers = new ArrayList<>();
    }

    public void attach(ObserverRI obsRI) throws RemoteException {
        System.out.println("ObserverImpl.attach(): obsRI = " + obsRI);
        if (!observers.contains(obsRI)) {
            observers.add(obsRI);
        }
    }

    ;

    public void detach(ObserverRI obsRI) throws RemoteException {
        System.out.println("ObserverImpl.detach(): obsRI = " + obsRI);
        observers.remove(obsRI);
    }

    ;

    public State getState() throws RemoteException {
        return this.state;
    }

    ;

    public void setState(State state) throws RemoteException {
        this.state = state;
        notifyAllObservers();
    }

    private void notifyAllObservers() {
        System.out.println("ObserverImpl.notifyAllObservers(): observers.size() = "+observers.size());
        for (ObserverRI observer : observers) {
            try {
                observer.update();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    ;
}
