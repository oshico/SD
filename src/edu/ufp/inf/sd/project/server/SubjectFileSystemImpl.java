package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.client.ObserverRI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SubjectFileSystemImpl extends UnicastRemoteObject implements SubjectFileSystemRI {
    private static final long serialVersionUID = 1L;

    private final String username;
    private final Database database;

    // Map of document name to list of observers
    private final Map<String, List<ObserverRI>> observers;

    // Map of document name to list of operations (for version history)
    private final Map<String, List<StateFileSystemOperation>> operationHistory;

    /**
     * Constructor for the file system implementation.
     *
     * @param username The username of the owner
     * @param database The database instance
     * @throws RemoteException If a remote communication error occurs
     */
    public SubjectFileSystemImpl(String username, Database database) throws RemoteException {
        super();
        this.username = username;
        this.database = database;
        this.observers = new ConcurrentHashMap<>();
        this.operationHistory = new ConcurrentHashMap<>();
    }

    /**
     * Creates a new document.
     *
     * @param documentName The name of the document
     * @return true if creation is successful, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public boolean createDocument(String documentName) throws RemoteException {
        boolean created = database.createDocument(username, documentName);

        if (created) {
            // Initialize observer list for this document
            observers.put(documentName, new ArrayList<>());

            // Record operation in history
            List<StateFileSystemOperation> history = new ArrayList<>();
            history.add(new StateFileSystemOperation(
                    username, documentName, "", StateFileSystemOperation.OperationType.CREATE));
            operationHistory.put(documentName, history);
        }

        return created;
    }

    /**
     * Opens an existing document.
     *
     * @param documentName The name of the document
     * @return The document content
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public String openDocument(String documentName) throws RemoteException {
        return database.getDocument(username, documentName);
    }

    /**
     * Saves a document with new content.
     *
     * @param documentName The document name
     * @param content      The new content
     * @return true if save is successful, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public boolean saveDocument(String documentName, String content) throws RemoteException {
        boolean updated = database.updateDocument(username, documentName, content);

        if (updated) {
            // Record operation in history
            List<StateFileSystemOperation> history = operationHistory.get(documentName);
            if (history == null) {
                history = new ArrayList<>();
                operationHistory.put(documentName, history);
            }

            history.add(new StateFileSystemOperation(
                    username, documentName, content, StateFileSystemOperation.OperationType.UPDATE));

            // Notify observers about the change
            updateDocument(documentName, content, username);
        }

        return updated;
    }

    /**
     * Lists all documents owned by the user.
     *
     * @return A list of document names
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public List<String> listDocuments() throws RemoteException {
        Map<String, String> docs = database.getUserDocuments(username);
        return new ArrayList<>(docs.keySet());
    }

    /**
     * Attaches an observer to a document.
     *
     * @param documentName The document name
     * @param observer     The observer to attach
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public void attachObserver(String documentName, ObserverRI observer) throws RemoteException {
        List<ObserverRI> docObservers = observers.computeIfAbsent(documentName, k -> new ArrayList<>());

        // Check if observer already exists to avoid duplicates
        boolean exists = false;
        for (ObserverRI existingObserver : docObservers) {
            try {
                // This is a simple equality check which may not work perfectly for remote objects
                if (existingObserver.equals(observer)) {
                    exists = true;
                    break;
                }
            } catch (RemoteException e) {
                // Remove failed observers
                docObservers.remove(existingObserver);
            }
        }

        if (!exists) {
            docObservers.add(observer);
        }
    }

    /**
     * Detaches an observer from a document.
     *
     * @param documentName The document name
     * @param observer     The observer to detach
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public void detachObserver(String documentName, ObserverRI observer) throws RemoteException {
        List<ObserverRI> docObservers = observers.get(documentName);
        if (docObservers != null) {
            // Remove observer (this is simplified and may not work perfectly for remote objects)
            docObservers.removeIf(o -> {
                try {
                    return o.equals(observer);
                } catch (RemoteException e) {
                    return true; // Remove failed observers
                }
            });
        }
    }

    /**
     * Updates a document and notifies all observers.
     *
     * @param documentName The document name
     * @param content      The new content
     * @param username     The username of the editor
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public void updateDocument(String documentName, String content, String username) throws RemoteException {
        List<ObserverRI> docObservers = observers.get(documentName);

        if (docObservers != null) {
            // Create a copy to avoid ConcurrentModificationException
            List<ObserverRI> observersCopy = new ArrayList<>(docObservers);
            List<ObserverRI> failedObservers = new ArrayList<>();

            // Notify all observers
            for (ObserverRI observer : observersCopy) {
                try {
                    observer.update(documentName, content, username);
                } catch (RemoteException e) {
                    System.err.println("Failed to notify observer: " + e.getMessage());
                    failedObservers.add(observer);
                }
            }

            // Remove failed observers
            docObservers.removeAll(failedObservers);
        }
    }
}
