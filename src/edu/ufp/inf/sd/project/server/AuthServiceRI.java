package edu.ufp.inf.sd.project.server;


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuthServiceRI extends Remote {

    SessionRI login(String username, String password) throws RemoteException;

    boolean register(String username, String password) throws RemoteException;
}
