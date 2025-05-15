package edu.ufp.inf.sd.project.server;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

public class SessionImpl extends UnicastRemoteObject implements SessionRI {
    @Serial
    private static final long serialVersionUID = 1L;

    private final ServerMain server;
    private final String username;
    private final String sessionId;
    private boolean valid;
    private final SubjectFileSystemRI fileSystem;

    /**
     * Constructor for the session implementation.
     *
     * @param server   The server instance
     * @param username The username associated with this session
     * @throws RemoteException If a remote communication error occurs
     */
    public SessionImpl(ServerMain server, String username) throws RemoteException {
        super();
        this.server = server;
        this.username = username;
        this.sessionId = UUID.randomUUID().toString();
        this.valid = true;

        // Create a file system for this user
        this.fileSystem = new SubjectFileSystemImpl(username, server.getDatabase());

        System.out.println("Created new session for user: " + username + " with ID: " + sessionId);
    }

    /**
     * Gets the username associated with this session.
     *
     * @return The username
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public String getUsername() throws RemoteException {
        checkValid();
        return username;
    }

    /**
     * Gets access to the file system operations.
     *
     * @return A SubjectFileSystemRI object
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public SubjectFileSystemRI getFileSystem() throws RemoteException {
        checkValid();
        return fileSystem;
    }

    /**
     * Logs out the current session.
     *
     * @return true if logout is successful, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public boolean logout() throws RemoteException {
        if (valid) {
            valid = false;
            server.removeActiveSession(username);
            System.out.println("User logged out: " + username);
            return true;
        }
        return false;
    }

    /**
     * Checks if the session is valid.
     *
     * @return true if the session is valid, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public boolean isValid() throws RemoteException {
        return valid;
    }

    /**
     * Gets the session ID.
     *
     * @return The session ID
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Invalidates the session.
     */
    public void invalidate() {
        this.valid = false;
        System.out.println("Session invalidated for user: " + username);
    }

    /**
     * Checks if the session is valid and throws an exception if not.
     *
     * @throws RemoteException If the session is not valid
     */
    private void checkValid() throws RemoteException {
        if (!valid) {
            throw new RemoteException("Session is not valid. Please login again.");
        }
    }
}
