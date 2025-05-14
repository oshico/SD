package edu.ufp.inf.sd.project.main.filesystem;

import java.io.Serial;
import java.io.Serializable;

public class File extends FileSystemItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private byte[] content;
    private String contentType;
    private long size;
    private String version;

    /**
     * Creates a new file.
     *
     * @param name        The name of the file
     * @param path        The full path of the file
     * @param owner       The username of the owner
     * @param content     The content of the file
     * @param contentType The MIME type of the file
     */
    public File(String name, String path, String owner, byte[] content, String contentType) {
        super(name, path, owner);
        this.content = content;
        this.contentType = contentType;
        this.size = content != null ? content.length : 0;
        this.version = "1.0";
    }

    /**
     * Gets the content of this file.
     *
     * @return The content as a byte array
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Sets the content of this file.
     *
     * @param content The new content
     */
    public void setContent(byte[] content) {
        this.content = content;
        this.size = content != null ? content.length : 0;
        updateLastModified();
        updateVersion();
    }

    /**
     * Gets the MIME type of this file.
     *
     * @return The content type
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the MIME type of this file.
     *
     * @param contentType The new content type
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
        updateLastModified();
    }

    /**
     * Gets the version of this file.
     *
     * @return The version string
     */
    public String getVersion() {
        return version;
    }

    /**
     * Updates the version of this file.
     */
    private void updateVersion() {
        // Simple version incrementing logic
        try {
            String[] parts = version.split("\\.");
            if (parts.length == 2) {
                int major = Integer.parseInt(parts[0]);
                int minor = Integer.parseInt(parts[1]);
                minor++;
                if (minor > 9) {
                    major++;
                    minor = 0;
                }
                this.version = major + "." + minor;
            } else {
                this.version = "1.1";
            }
        } catch (NumberFormatException e) {
            this.version = "1.1";
        }
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public boolean isFolder() {
        return false;
    }

    @Override
    public String toString() {
        return "File{" +
                "name='" + getName() + '\'' +
                ", path='" + getPath() + '\'' +
                ", owner='" + getOwner() + '\'' +
                ", size=" + size +
                ", contentType='" + contentType + '\'' +
                ", version='" + version + '\'' +
                ", creationDate=" + getCreationDate() +
                ", lastModifiedDate=" + getLastModifiedDate() +
                '}';
    }
}
