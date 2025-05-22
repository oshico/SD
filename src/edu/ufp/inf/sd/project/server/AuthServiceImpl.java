package edu.ufp.inf.sd.project.server;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AuthServiceImpl extends UnicastRemoteObject implements AuthServiceRI {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Database database;

    public AuthServiceImpl(Database database) throws RemoteException {
        super();
        this.database = database;
    }

    @Override
    public SessionRI login(String username, String password) throws RemoteException {
        System.out.println("Login attempt: " + username);

        if (database.authenticateUser(username, password)) {
            System.out.println("Login successful for user: " + username);
            SessionImpl session = new SessionImpl(username, database);
            database.addSession(username, session);
            return session;
        } else {
            System.out.println("Login failed for user: " + username);
            return null;
        }
    }

    @Override
    public boolean register(String username, String password) throws RemoteException {
        System.out.println("Registration attempt: " + username);

        if (!username.matches("^[a-zA-Z0-9]+$")) {
            System.out.println("Invalid username format: " + username);
            return false;
        }

        if (password.length() < 4) {
            System.out.println("Password too short");
            return false;
        }

        boolean success = database.addUser(username, password);
        boolean addedFileSystem = database.addSubjectFileSystem(username, new SubjectFileSystemImpl(username, database));

        if (success && addedFileSystem) {
            System.out.println("Registration successful for user: " + username);

        } else {
            System.out.println("Registration failed for user: " + username + " (username may already exist)");
        }

        return success;
    }
}
