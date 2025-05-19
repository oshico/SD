package edu.ufp.inf.sd.project.client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ObserverRI extends Remote {
    /**
     * Method called by the server to update the client about document changes.
     *
     * @param documentName   The name of the document that was updated
     * @param content        The new content of the document
     * @param editorUsername The username of the user who made the edit
     * @throws RemoteException If a remote communication error occurs
     */
    void update(String documentName, String content, String editorUsername) throws RemoteException;

    /**
     * Method called by the server to notify the client about a newly shared folder.
     *
     * @param ownerUsername The username of the document owner
     * @param folderName    The name of the shared folder
     * @param documentNames The list of document names in the shared folder
     * @throws RemoteException If a remote communication error occurs
     */
    void notifySharedFolder(String ownerUsername, String folderName, List<String> documentNames) throws RemoteException;

    /**
     * Method called by the server to update the client about changes to a shared document.
     *
     * @param ownerUsername  The username of the document owner
     * @param folderName     The name of the shared folder
     * @param documentName   The name of the document that was updated
     * @param content        The new content of the document
     * @param editorUsername The username of the user who made the edit
     * @throws RemoteException If a remote communication error occurs
     */
    void updateSharedDocument(String ownerUsername, String folderName, String documentName,
                              String content, String editorUsername) throws RemoteException;

    /**
     * Method called by the server to notify the client that a folder is no longer shared with them.
     *
     * @param ownerUsername The username of the folder owner
     * @param folderName    The name of the unshared folder
     * @throws RemoteException If a remote communication error occurs
     */
    void notifyUnsharedFolder(String ownerUsername, String folderName) throws RemoteException;
}
