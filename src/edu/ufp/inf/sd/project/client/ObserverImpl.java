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
}
