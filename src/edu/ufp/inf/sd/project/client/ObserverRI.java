package edu.ufp.inf.sd.project.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ObserverRI extends Remote{
    /**
     * Method called by the server to update the client about document changes.
     *
     * @param documentName The name of the document that was updated
     * @param content The new content of the document
     * @param editorUsername The username of the user who made the edit
     * @throws RemoteException If a remote communication error occurs
     */
    void update(String documentName, String content, String editorUsername) throws RemoteException;
}
