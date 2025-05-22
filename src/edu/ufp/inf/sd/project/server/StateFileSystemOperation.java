package edu.ufp.inf.sd.project.server;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class StateFileSystemOperation implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;

    private final String username;
    private final String documentName;
    private final String content;
    private final LocalDateTime timestamp;
    private final OperationType type;


    public enum OperationType {
        CREATE,
        UPDATE,
        DELETE
    }

    /**
     * Constructor for creating a new file system operation.
     *
     * @param username     The username of the user performing the operation
     * @param documentName The name of the document
     * @param content      The content of the document after the operation
     * @param type         The type of operation
     */
    public StateFileSystemOperation(String username, String documentName, String content, OperationType type) {
        this.username = username;
        this.documentName = documentName;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.type = type;
    }

    /**
     * Gets the username.
     *
     * @return The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the document name.
     *
     * @return The document name
     */
    public String getDocumentName() {
        return documentName;
    }

    /**
     * Gets the document content.
     *
     * @return The document content
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets the timestamp of the operation.
     *
     * @return The timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the operation type.
     *
     * @return The operation type
     */
    public OperationType getType() {
        return type;
    }
}
