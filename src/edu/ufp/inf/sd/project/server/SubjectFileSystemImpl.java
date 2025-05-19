package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.client.ObserverRI;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SubjectFileSystemImpl extends UnicastRemoteObject implements SubjectFileSystemRI {
    @Serial
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
            } catch (Exception e) {
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
                } catch (Exception e) {
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

    /**
     * Creates a new folder.
     *
     * @param folderName The name of the folder
     * @return true if creation is successful, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public boolean createFolder(String folderName) throws RemoteException {
        System.out.println("Creating folder: " + folderName + " for user: " + username);
        return database.createFolder(username, folderName);
    }

    /**
     * Lists all folders owned by the user.
     *
     * @return A list of folder names
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public List<String> listFolders() throws RemoteException {
        Map<String, Folder> folders = database.getUserFolders(username);
        return new ArrayList<>(folders.keySet());
    }

    /**
     * Lists all folders shared with the user.
     *
     * @return A map where keys are owner usernames and values are lists of folder names
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public Map<String, List<String>> listSharedFolders() throws RemoteException {
        Map<String, Map<String, Folder>> sharedFolders = database.getSharedFoldersWithUser(username);
        Map<String, List<String>> result = new HashMap<>();

        for (Map.Entry<String, Map<String, Folder>> entry : sharedFolders.entrySet()) {
            String ownerUsername = entry.getKey();
            Map<String, Folder> ownerFolders = entry.getValue();
            List<String> folderNames = new ArrayList<>(ownerFolders.keySet());
            result.put(ownerUsername, folderNames);
        }

        return result;
    }

    /**
     * Adds a document to a folder.
     *
     * @param folderName   The name of the folder
     * @param documentName The name of the document
     * @return true if the document was added to the folder, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public boolean addDocumentToFolder(String folderName, String documentName) throws RemoteException {
        System.out.println("Adding document: " + documentName + " to folder: " + folderName + " for user: " + username);
        return database.addDocumentToFolder(username, folderName, documentName);
    }

    /**
     * Removes a document from a folder.
     *
     * @param folderName   The name of the folder
     * @param documentName The name of the document
     * @return true if the document was removed from the folder, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public boolean removeDocumentFromFolder(String folderName, String documentName) throws RemoteException {
        System.out.println("Removing document: " + documentName + " from folder: " + folderName + " for user: " + username);
        return database.removeDocumentFromFolder(username, folderName, documentName);
    }

    /**
     * Lists all documents in a folder.
     *
     * @param folderName The name of the folder
     * @return A list of document names
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public List<String> listFolderDocuments(String folderName) throws RemoteException {
        Folder folder = database.getFolder(username, folderName);
        if (folder != null) {
            return folder.getDocuments();
        }
        return new ArrayList<>();
    }

    /**
     * Lists all documents in a shared folder.
     *
     * @param ownerUsername The username of the folder owner
     * @param folderName    The name of the folder
     * @return A list of document names
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public List<String> listSharedFolderDocuments(String ownerUsername, String folderName) throws RemoteException {
        Folder folder = database.getFolder(ownerUsername, folderName);
        if (folder != null && folder.hasAccess(username)) {
            return folder.getDocuments();
        }
        return new ArrayList<>();
    }

    /**
     * Shares a folder with another user.
     *
     * @param folderName     The name of the folder
     * @param targetUsername The username to share with
     * @return true if the folder was shared, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public boolean shareFolder(String folderName, String targetUsername) throws RemoteException {
        System.out.println("User " + username + " is sharing folder: " + folderName + " with user: " + targetUsername);
        return database.shareFolder(username, folderName, targetUsername);
    }

    /**
     * Unshares a folder with a user.
     *
     * @param folderName     The name of the folder
     * @param targetUsername The username to unshare with
     * @return true if the folder was unshared, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public boolean unshareFolder(String folderName, String targetUsername) throws RemoteException {
        System.out.println("User " + username + " is unsharing folder: " + folderName + " with user: " + targetUsername);
        return database.unshareFolder(username, folderName, targetUsername);
    }

    /**
     * Opens a document from a shared folder.
     *
     * @param ownerUsername The username of the folder owner
     * @param folderName    The name of the folder
     * @param documentName  The name of the document
     * @return The document content as a String
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public String openSharedDocument(String ownerUsername, String folderName, String documentName) throws RemoteException {
        // Check if the user has access to the folder
        Folder folder = database.getFolder(ownerUsername, folderName);
        if (folder != null && folder.hasAccess(username)) {
            // Check if the document is in the folder
            if (folder.getDocuments().contains(documentName)) {
                return database.getDocument(ownerUsername, documentName);
            }
        }
        throw new RemoteException("Access denied or document not found in the shared folder");
    }
}
