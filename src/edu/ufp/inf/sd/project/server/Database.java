package edu.ufp.inf.sd.project.server;

import java.io.*;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Database implements Serializable {

    private static Database instance;

    @Serial
    private static final long serialVersionUID = 1L;

    private final Map<String, User> users;
    private final Map<String, SessionRI> sessions;
    private final Map<String, SubjectFileSystemRI> subjectFileSystems;
    private final Map<String, Map<String, SubjectFileSystemRI>> userSharedFolders; // username -> (owner -> set of folderNames)

    /**
     * Constructor that initializes the database.
     * Attempts to load existing data from file if available.
     */
    private Database() {
        // Initialize with concurrent collections for thread safety
        users = new ConcurrentHashMap<>();
        sessions = new ConcurrentHashMap<>();
        subjectFileSystems = new ConcurrentHashMap<>();
        userSharedFolders = new ConcurrentHashMap<>();
    }

    protected static Database getDatabaseInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    /**
     * Adds a new user to the database.
     *
     * @param username The username
     * @param password The password (should be hashed in production)
     * @return true if user was added successfully, false if username already exists
     */
    public synchronized boolean addUser(String username, String password) {
        if (users.containsKey(username)) {
            return false;
        }
        users.put(username, new User(username, password));
        return true;
    }

    /**
     * Gets a user by username.
     *
     * @param username The username
     * @return The User object, or null if not found
     */
    public User getUser(String username) {
        return users.get(username);
    }

    /**
     * Authenticates a user with username and password.
     *
     * @param username The username
     * @param password The password
     * @return true if authentication is successful, false otherwise
     */
    public boolean authenticateUser(String username, String password) {
        User user = getUser(username);
        return user != null && user.validatePassword(password);
    }

    public synchronized boolean addSession(String username, SessionRI session) throws RemoteException {
        if (sessions.containsKey(username)) {
            return false;
        }
        sessions.put(username, session);
        return true;
    }

    public SessionRI getSession(String username) throws RemoteException {
        if (!sessions.containsKey(username)) {
            return null;
        }
        return sessions.get(username);
    }

    public synchronized boolean removeSession(String username) throws RemoteException {
        if (!sessions.containsKey(username)) {
            return false;
        }
        sessions.remove(username);
        return true;
    }

    public synchronized boolean addSubjectFileSystem(String username, SubjectFileSystemRI fileSystem) throws RemoteException {
        if (subjectFileSystems.containsKey(username)) {
            return false;
        }
        subjectFileSystems.put(username, fileSystem);
        return true;
    }

    public SubjectFileSystemRI getSubjectFileSystem(String username) throws RemoteException {
        if (!subjectFileSystems.containsKey(username)) {
            return null;
        }
        return subjectFileSystems.get(username);
    }

    public synchronized boolean removeSubjectFileSystem(String username) throws RemoteException {
        if (!subjectFileSystems.containsKey(username)) {
            return false;
        }
        subjectFileSystems.remove(username);
        return true;
    }

    public synchronized boolean addUserSharedFolder(String username, String owner, SubjectFileSystemRI subjectFileSystem) throws RemoteException {
        if (!userSharedFolders.containsKey(username)) {
            userSharedFolders.put(username, new HashMap<>());
        }
        Map<String, SubjectFileSystemRI> sharedFolders = new HashMap<>();
        sharedFolders.put(owner, subjectFileSystem);
        userSharedFolders.put(username, sharedFolders);
        return true;
    }

    public synchronized Map<String, SubjectFileSystemRI> getUserSharedFolders(String username) throws RemoteException {
        if (!userSharedFolders.containsKey(username)) {
            return null;
        }
        return userSharedFolders.get(username);
    }

    public synchronized boolean removeUserSharedFolder(String owner, String username, SubjectFileSystemRI subjectFileSystem) throws RemoteException {
        if (!userSharedFolders.containsKey(owner)) {
            return false;
        }
        userSharedFolders.remove(username, subjectFileSystem);
        return true;
    }
}
