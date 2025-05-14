package edu.ufp.inf.sd.project.main.filesystem;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Folder extends FileSystemItem implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;

    private final List<FileSystemItem> items;
    private final List<String> sharedWith;
    private boolean isRoot; // Indicates if this is a root folder

    /**
     * Creates a new folder.
     *
     * @param name  The name of the folder
     * @param path  The full path of the folder
     * @param owner The username of the owner
     */
    public Folder(String name, String path, String owner) {
        super(name, path, owner);
        this.items = new CopyOnWriteArrayList<>(); // Thread-safe list for concurrent operations
        this.sharedWith = new CopyOnWriteArrayList<>();
        this.isRoot = false;
    }

    /**
     * Creates a new root folder for a user.
     *
     * @param owner The username of the owner
     * @return A new root folder
     */
    public static Folder createRootFolder(String owner) {
        Folder folder = new Folder(owner + "_root", "/" + owner, owner);
        folder.isRoot = true;
        return folder;
    }

    /**
     * Creates a new shared folder for a user.
     *
     * @param owner The username of the owner
     * @return A new shared folder
     */
    public static Folder createSharedFolder(String owner) {
        Folder folder = new Folder(owner + "_shared", "/" + owner + "/shared", owner);
        return folder;
    }

    /**
     * Adds an item to this folder.
     *
     * @param item The item to add
     * @return True if the item was added, false otherwise
     */
    public synchronized boolean addItem(FileSystemItem item) {
        if (containsItem(item.getName())) {
            return false;
        }
        items.add(item);
        updateLastModified();
        return true;
    }

    /**
     * Removes an item from this folder.
     *
     * @param itemName The name of the item to remove
     * @return The removed item, or null if not found
     */
    public synchronized FileSystemItem removeItem(String itemName) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getName().equals(itemName)) {
                FileSystemItem removed = items.remove(i);
                updateLastModified();
                return removed;
            }
        }
        return null;
    }

    /**
     * Gets an item from this folder.
     *
     * @param itemName The name of the item to get
     * @return The item, or null if not found
     */
    public FileSystemItem getItem(String itemName) {
        for (FileSystemItem item : items) {
            if (item.getName().equals(itemName)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Checks if this folder contains an item with the given name.
     *
     * @param itemName The name of the item
     * @return True if the folder contains an item with the given name, false otherwise
     */
    public boolean containsItem(String itemName) {
        for (FileSystemItem item : items) {
            if (item.getName().equals(itemName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets all items in this folder.
     *
     * @return A list of all items
     */
    public List<FileSystemItem> getItems() {
        return new ArrayList<>(items); // Return a copy to prevent concurrent modification
    }

    /**
     * Shares this folder with another user.
     *
     * @param username The username of the user to share with
     * @return True if the folder was shared, false if it was already shared
     */
    public boolean shareWith(String username) {
        if (!sharedWith.contains(username)) {
            sharedWith.add(username);
            updateLastModified();
            return true;
        }
        return false;
    }

    /**
     * Unshares this folder from another user.
     *
     * @param username The username of the user to unshare from
     * @return True if the folder was unshared, false if it wasn't shared
     */
    public boolean unshareFrom(String username) {
        boolean removed = sharedWith.remove(username);
        if (removed) {
            updateLastModified();
        }
        return removed;
    }

    /**
     * Checks if this folder is shared with a user.
     *
     * @param username The username of the user
     * @return True if the folder is shared with the user, false otherwise
     */
    public boolean isSharedWith(String username) {
        return sharedWith.contains(username);
    }

    /**
     * Gets all users this folder is shared with.
     *
     * @return A list of usernames
     */
    public List<String> getSharedWith() {
        return new ArrayList<>(sharedWith);
    }

    /**
     * Checks if this folder is a root folder.
     *
     * @return True if this is a root folder, false otherwise
     */
    public boolean isRoot() {
        return isRoot;
    }

    @Override
    public long getSize() {
        long size = 0;
        for (FileSystemItem item : items) {
            size += item.getSize();
        }
        return size;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public boolean isFolder() {
        return true;
    }

    @Override
    public String toString() {
        return "Folder{" +
                "name='" + getName() + '\'' +
                ", path='" + getPath() + '\'' +
                ", owner='" + getOwner() + '\'' +
                ", items=" + items.size() +
                ", sharedWith=" + sharedWith.size() +
                ", isRoot=" + isRoot +
                ", creationDate=" + getCreationDate() +
                ", lastModifiedDate=" + getLastModifiedDate() +
                '}';
    }
}
