package edu.ufp.inf.sd.project.main.filesystem;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public abstract class FileSystemItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String path;
    private final String owner;
    private final Date creationDate;
    private Date lastModifiedDate;

    /**
     * Creates a new file system item.
     *
     * @param name  The name of the item
     * @param path  The full path of the item
     * @param owner The username of the owner
     */
    public FileSystemItem(String name, String path, String owner) {
        this.name = name;
        this.path = path;
        this.owner = owner;
        this.creationDate = new Date();
        this.lastModifiedDate = new Date();
    }

    /**
     * Gets the name of this item.
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this item.
     *
     * @param name The new name
     */
    public void setName(String name) {
        this.name = name;
        updateLastModified();
    }

    /**
     * Gets the full path of this item.
     *
     * @return The path
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the full path of this item.
     *
     * @param path The new path
     */
    public void setPath(String path) {
        this.path = path;
        updateLastModified();
    }

    /**
     * Gets the username of the owner.
     *
     * @return The owner's username
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Gets the creation date of this item.
     *
     * @return The creation date
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Gets the last modified date of this item.
     *
     * @return The last modified date
     */
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    /**
     * Updates the last modified date to the current time.
     */
    protected void updateLastModified() {
        this.lastModifiedDate = new Date();
    }

    /**
     * Gets the size of this item.
     *
     * @return The size in bytes
     */
    public abstract long getSize();

    /**
     * Checks if this item is a file.
     *
     * @return True if this item is a file, false otherwise
     */
    public abstract boolean isFile();

    /**
     * Checks if this item is a folder.
     *
     * @return True if this item is a folder, false otherwise
     */
    public abstract boolean isFolder();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FileSystemItem that = (FileSystemItem) obj;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public String toString() {
        return "FileSystemItem{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", owner='" + owner + '\'' +
                ", creationDate=" + creationDate +
                ", lastModifiedDate=" + lastModifiedDate +
                '}';
    }
}
