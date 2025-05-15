package edu.ufp.inf.sd.project.server;


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuthServiceRI extends Remote {
    /**
     * Authenticates a user with username and password.
     *
     * @param username The username of the user
     * @param password The password of the user
     * @return A SessionRI object if authentication is successful, null otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    SessionRI login(String username, String password) throws RemoteException;

    /**
     * Registers a new user with the given credentials.
     *
     * @param username The username for the new user
     * @param password The password for the new user
     * @return true if registration is successful, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    boolean register(String username, String password) throws RemoteException;
}
