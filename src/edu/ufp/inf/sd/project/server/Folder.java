package edu.ufp.inf.sd.project.server;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Folder implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String owner;
    private final Set<String> sharedWith;
    private final List<String> documents;
    private boolean isShared;

    /**
     * Constructor for creating a new folder.
     *
     * @param name  The name of the folder
     * @param owner The username of the folder owner
     */
    public Folder(String name, String owner) {
        this.name = name;
        this.owner = owner;
        this.sharedWith = new HashSet<>();
        this.documents = new ArrayList<>();
        this.isShared = false;
    }

    /**
     * Gets the name of the folder.
     *
     * @return The folder name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the username of the folder owner.
     *
     * @return The owner's username
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Checks if the folder is shared.
     *
     * @return true if the folder is shared, false otherwise
     */
    public boolean isShared() {
        return isShared;
    }

    /**
     * Sets the shared status of the folder.
     *
     * @param shared The shared status
     */
    public void setShared(boolean shared) {
        this.isShared = shared;
    }

    /**
     * Gets the set of usernames the folder is shared with.
     *
     * @return A set of usernames
     */
    public Set<String> getSharedWith() {
        return new HashSet<>(sharedWith);
    }

    /**
     * Shares the folder with another user.
     *
     * @param username The username to share with
     * @return true if the folder was newly shared with this user, false if it was already shared
     */
    public boolean shareWith(String username) {
        if (sharedWith.add(username)) {
            isShared = true;
            return true;
        }
        return false;
    }

    /**
     * Unshares the folder with a user.
     *
     * @param username The username to unshare with
     * @return true if the folder was shared with this user, false otherwise
     */
    public boolean unshareWith(String username) {
        return sharedWith.remove(username);
    }

    /**
     * Adds a document to the folder.
     *
     * @param documentName The name of the document to add
     * @return true if the document was added, false if it already exists in the folder
     */
    public boolean addDocument(String documentName) {
        if (!documents.contains(documentName)) {
            documents.add(documentName);
            return true;
        }
        return false;
    }

    /**
     * Removes a document from the folder.
     *
     * @param documentName The name of the document to remove
     * @return true if the document was removed, false if it wasn't in the folder
     */
    public boolean removeDocument(String documentName) {
        return documents.remove(documentName);
    }

    /**
     * Gets a list of documents in the folder.
     *
     * @return A list of document names
     */
    public List<String> getDocuments() {
        return new ArrayList<>(documents);
    }

    /**
     * Checks if a user has access to this folder.
     *
     * @param username The username to check
     * @return true if the user is the owner or the folder is shared with them, false otherwise
     */
    public boolean hasAccess(String username) {
        return owner.equals(username) || sharedWith.contains(username);
    }
}
