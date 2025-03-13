package edu.ufp.inf.sd.rmi.p05_observer.client;

import edu.ufp.inf.sd.rmi.p05_observer.server.State;
import edu.ufp.inf.sd.rmi.p05_observer.server.SubjectRI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ObserverImpl extends UnicastRemoteObject implements ObserverRI {


    private final SubjectRI subjectRI;
    private final ObserverGuiClient observerGuiClient;
    private final String username;
    private State lastState;

    public ObserverImpl(String username, ObserverGuiClient observer, SubjectRI subjectRI) throws RemoteException {
        super();
        this.observerGuiClient = observer;
        this.username = username;
        this.subjectRI = subjectRI;
        this.subjectRI.attach(this);
    }

    public void update() throws RemoteException {
        System.out.println("ObserverImpl.update()...");
        this.lastState = subjectRI.getState();
        System.out.println("ObserverImpl.update(): this.lastState = " + this.lastState);
        observerGuiClient.updateTextArea();
    }

    public State getLastState() {
        return lastState;
    }

    public SubjectRI getSubjectRI() {
        return subjectRI;
    }
}
