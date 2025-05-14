package edu.ufp.inf.sd.project.main.user;


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuthService extends Remote {
    /**
     * Registers a new user with the given username and password.
     *
     * @param username The username for the new user
     * @param password The password for the new user
     * @return True if registration was successful, false otherwise (e.g., username already taken)
     * @throws RemoteException If a remote communication error occurs
     */
    boolean registerUser(String username, String password) throws RemoteException;

    /**
     * Authenticates a user with the given username and password.
     *
     * @param username The username of the user
     * @param password The password of the user
     * @return A session ID if authentication was successful, null otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    String login(String username, String password) throws RemoteException;

    /**
     * Validates a session with the given session ID.
     *
     * @param sessionId The session ID to validate
     * @return The username associated with the session if valid, null otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    String validateSession(String sessionId) throws RemoteException;

    /**
     * Logs out a user by invalidating their session.
     *
     * @param sessionId The session ID to invalidate
     * @return True if logout was successful, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    boolean logout(String sessionId) throws RemoteException;

    /**
     * Changes the password for a user.
     *
     * @param sessionId   The session ID of the authenticated user
     * @param oldPassword The old password
     * @param newPassword The new password
     * @return True if password change was successful, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    boolean changePassword(String sessionId, String oldPassword, String newPassword) throws RemoteException;
}
