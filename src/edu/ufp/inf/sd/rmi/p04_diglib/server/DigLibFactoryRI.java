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
public interface DigLibFactoryRI extends Remote {
    public boolean register(String uname, String pw) throws RemoteException;
    public DigLibSessionRI login(String uname, String pw) throws RemoteException;
}
