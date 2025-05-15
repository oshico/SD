package edu.ufp.inf.sd.project.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import edu.ufp.inf.sd.project.client.ObserverRI;

public interface SubjectFileSystemRI extends Remote {
    /**
     * Creates a new document with the given name.
     *
     * @param documentName The name of the document
     * @return true if creation is successful, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    boolean createDocument(String documentName) throws RemoteException;

    /**
     * Opens an existing document.
     *
     * @param documentName The name of the document to open
     * @return The document content as a String
     * @throws RemoteException If a remote communication error occurs
     */
    String openDocument(String documentName) throws RemoteException;

    /**
     * Saves the document with the given content.
     *
     * @param documentName The name of the document
     * @param content      The new content to save
     * @return true if save is successful, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    boolean saveDocument(String documentName, String content) throws RemoteException;

    /**
     * Gets a list of all document names available to the user.
     *
     * @return A list of document names
     * @throws RemoteException If a remote communication error occurs
     */
    List<String> listDocuments() throws RemoteException;

    /**
     * Registers an observer for document updates.
     *
     * @param documentName The name of the document to observe
     * @param observer     The observer to register
     * @throws RemoteException If a remote communication error occurs
     */
    void attachObserver(String documentName, ObserverRI observer) throws RemoteException;

    /**
     * Unregisters an observer from document updates.
     *
     * @param documentName The name of the document
     * @param observer     The observer to unregister
     * @throws RemoteException If a remote communication error occurs
     */
    void detachObserver(String documentName, ObserverRI observer) throws RemoteException;

    /**
     * Updates a document with new content and notifies all observers.
     *
     * @param documentName The name of the document
     * @param content      The new content
     * @param username     The username of the user making the update
     * @throws RemoteException If a remote communication error occurs
     */
    void updateDocument(String documentName, String content, String username) throws RemoteException;
}
