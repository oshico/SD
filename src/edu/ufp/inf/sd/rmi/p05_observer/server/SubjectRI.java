/**
 * <p>Title: Projecto SD</p>
 * <p>Description: Projecto apoio aulas SD</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: UFP </p>
 * @author Rui Moreira
 * @version 2.0
 */
package edu.ufp.inf.sd.rmi.p05_observer.server;

import edu.ufp.inf.sd.rmi.p05_observer.client.ObserverRI;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 
 * @author rui
 */
public interface SubjectRI extends Remote {
    public void attach(ObserverRI obsRI) throws RemoteException;
    public void detach(ObserverRI obsRI) throws RemoteException;
    public State getState() throws RemoteException;
    public void setState(State state) throws RemoteException;
}
