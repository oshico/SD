package edu.ufp.inf.sd.project.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import edu.ufp.inf.sd.project.client.ObserverRI;

public interface SessionRI extends Remote {
    /**
     * Gets the username associated with this session.
     *
     * @return The username
     * @throws RemoteException If a remote communication error occurs
     */
    String getUsername() throws RemoteException;

    /**
     * Gets access to the file system operations.
     *
     * @return A SubjectFileSystemRI object
     * @throws RemoteException If a remote communication error occurs
     */
    SubjectFileSystemRI getFileSystem() throws RemoteException;

    /**
     * Logs out the current session.
     *
     * @return true if logout is successful, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    boolean logout() throws RemoteException;

    /**
     * Checks if the session is valid.
     *
     * @return true if the session is valid, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    boolean isValid() throws RemoteException;
}
