/**
 * <p>Title: Projecto SD</p>
 * <p>Description: Projecto apoio aulas SD</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: UFP </p>
 * @author Rui Moreira
 * @version 2.0
 */
package edu.ufp.inf.sd.rmi.p05_observer.client;

import java.rmi.*;

/**
 * 
 * @author rui
 */
public interface ObserverRI extends Remote {
    public void update() throws RemoteException;
}
