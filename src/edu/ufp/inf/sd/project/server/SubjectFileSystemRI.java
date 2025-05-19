package edu.ufp.inf.sd.project.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

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

    /**
     * Creates a new folder.
     *
     * @param folderName The name of the folder
     * @return true if creation is successful, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    boolean createFolder(String folderName) throws RemoteException;

    /**
     * Lists all folders owned by the user.
     *
     * @return A list of folder names
     * @throws RemoteException If a remote communication error occurs
     */
    List<String> listFolders() throws RemoteException;

    /**
     * Lists all folders shared with the user.
     *
     * @return A map where keys are owner usernames and values are lists of folder names
     * @throws RemoteException If a remote communication error occurs
     */
    Map<String, List<String>> listSharedFolders() throws RemoteException;

    /**
     * Adds a document to a folder.
     *
     * @param folderName   The name of the folder
     * @param documentName The name of the document
     * @return true if the document was added to the folder, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    boolean addDocumentToFolder(String folderName, String documentName) throws RemoteException;

    /**
     * Removes a document from a folder.
     *
     * @param folderName   The name of the folder
     * @param documentName The name of the document
     * @return true if the document was removed from the folder, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    boolean removeDocumentFromFolder(String folderName, String documentName) throws RemoteException;

    /**
     * Lists all documents in a folder.
     *
     * @param folderName The name of the folder
     * @return A list of document names
     * @throws RemoteException If a remote communication error occurs
     */
    List<String> listFolderDocuments(String folderName) throws RemoteException;

    /**
     * Lists all documents in a shared folder.
     *
     * @param ownerUsername The username of the folder owner
     * @param folderName    The name of the folder
     * @return A list of document names
     * @throws RemoteException If a remote communication error occurs
     */
    List<String> listSharedFolderDocuments(String ownerUsername, String folderName) throws RemoteException;

    /**
     * Shares a folder with another user.
     *
     * @param folderName The name of the folder
     * @param username   The username to share with
     * @return true if the folder was shared, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    boolean shareFolder(String folderName, String username) throws RemoteException;

    /**
     * Unshares a folder with a user.
     *
     * @param folderName The name of the folder
     * @param username   The username to unshare with
     * @return true if the folder was unshared, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    boolean unshareFolder(String folderName, String username) throws RemoteException;

    /**
     * Opens a document from a shared folder.
     *
     * @param ownerUsername The username of the folder owner
     * @param folderName    The name of the folder
     * @param documentName  The name of the document
     * @return The document content as a String
     * @throws RemoteException If a remote communication error occurs
     */
    String openSharedDocument(String ownerUsername, String folderName, String documentName) throws RemoteException;
}
