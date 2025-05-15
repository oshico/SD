package edu.ufp.inf.sd.project.server;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AuthServiceImpl extends UnicastRemoteObject implements AuthServiceRI {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Database database;
    private final ServerMain server;

    /**
     * Constructor for the authentication service implementation.
     *
     * @param server   The server instance
     * @param database The database instance
     * @throws RemoteException If a remote communication error occurs
     */
    public AuthServiceImpl(ServerMain server, Database database) throws RemoteException {
        super();
        this.server = server;
        this.database = database;
    }

    /**
     * Implements user authentication by checking credentials against the database.
     *
     * @param username The username of the user
     * @param password The password of the user
     * @return A SessionRI object if authentication is successful, null otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public SessionRI login(String username, String password) throws RemoteException {
        System.out.println("Login attempt: " + username);

        if (database.authenticateUser(username, password)) {
            System.out.println("Login successful for user: " + username);

            // Create a new session for the authenticated user
            SessionImpl session = new SessionImpl(server, username);
            server.addActiveSession(username, session);

            return session;
        } else {
            System.out.println("Login failed for user: " + username);
            return null;
        }
    }

    /**
     * Implements user registration by adding a new user to the database.
     *
     * @param username The username for the new user
     * @param password The password for the new user
     * @return true if registration is successful, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    @Override
    public boolean register(String username, String password) throws RemoteException {
        System.out.println("Registration attempt: " + username);

        // Username validation - only alphanumeric characters allowed
        if (!username.matches("^[a-zA-Z0-9]+$")) {
            System.out.println("Invalid username format: " + username);
            return false;
        }

        // Password validation - minimum length of 4 characters
        if (password.length() < 4) {
            System.out.println("Password too short");
            return false;
        }

        // Add user to database
        boolean success = database.addUser(username, password);

        if (success) {
            System.out.println("Registration successful for user: " + username);
        } else {
            System.out.println("Registration failed for user: " + username + " (username may already exist)");
        }

        return success;
    }
}
