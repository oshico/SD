package edu.ufp.inf.sd.project.client;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ObserverImpl extends UnicastRemoteObject implements ObserverRI {
    private final ClientMain client;

    /**
     * Constructor for the observer implementation.
     *
     * @param client The client instance that owns this observer
     * @throws RemoteException If a remote communication error occurs
     */
    public ObserverImpl(ClientMain client) throws RemoteException {
        super();
        this.client = client;
    }

    /**
     * Called by the server when a document is updated.
     * Updates the client's local view of the document.
     *
     * @param documentName   The name of the document that was updated
     * @param content        The new content of the document
     * @param editorUsername The username of the user who made the edit
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public void update(String documentName, String content, String editorUsername) throws RemoteException {
        // Skip own updates to prevent update loops
        if (editorUsername.equals(client.getCurrentUsername())) {
            return;
        }

        System.out.println("Document '" + documentName + "' updated by " + editorUsername);

        // Update client's local copy if it's the currently open document
        if (client.getCurrentDocument() != null &&
                client.getCurrentDocument().equals(documentName)) {
            client.updateDocumentContent(content);
        }
    }

    /**
     * Called by the server when a document is shared with the user.
     * Notifies the client about the newly shared document.
     *
     * @param ownerUsername The username of the document owner
     * @param folderName    The name of the shared folder
     * @param documentNames The list of document names in the shared folder
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public void notifySharedFolder(String ownerUsername, String folderName, java.util.List<String> documentNames) throws RemoteException {
        System.out.println("Folder '" + folderName + "' shared by " + ownerUsername);

        // Notify the client about the newly shared folder
        client.handleNewSharedFolder(ownerUsername, folderName, documentNames);
    }

    /**
     * Called by the server when a document in a shared folder is updated.
     * Updates the client's local view if the document is currently open.
     *
     * @param ownerUsername  The username of the document owner
     * @param folderName     The name of the shared folder
     * @param documentName   The name of the document that was updated
     * @param content        The new content of the document
     * @param editorUsername The username of the user who made the edit
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public void updateSharedDocument(String ownerUsername, String folderName, String documentName,
                                     String content, String editorUsername) throws RemoteException {
        // Skip own updates to prevent update loops
        if (editorUsername.equals(client.getCurrentUsername())) {
            return;
        }

        System.out.println("Shared document '" + documentName + "' in folder '" +
                folderName + "' by " + ownerUsername + " updated by " + editorUsername);

        // Update client's local copy if it's the currently open shared document
        if (client.isCurrentSharedDocument(ownerUsername, folderName, documentName)) {
            client.updateDocumentContent(content);
        }
    }

    /**
     * Called by the server when a folder is unshared with the user.
     * Notifies the client to remove the folder from its shared folders list.
     *
     * @param ownerUsername The username of the folder owner
     * @param folderName    The name of the unshared folder
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public void notifyUnsharedFolder(String ownerUsername, String folderName) throws RemoteException {
        System.out.println("Folder '" + folderName + "' by " + ownerUsername + " is no longer shared with you");

        // Notify the client to remove the unshared folder
        client.handleUnsharedFolder(ownerUsername, folderName);
    }
}
