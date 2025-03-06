/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ufp.inf.sd.rmi.p04_diglib.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author rmoreira
 */
public interface DigLibSessionRI extends Remote {
    public Book[] search(String title, String author) throws RemoteException;
    public void logout() throws RemoteException;
}
