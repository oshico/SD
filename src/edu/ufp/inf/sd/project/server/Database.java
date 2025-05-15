package edu.ufp.inf.sd.project.server;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Database implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String DB_FILE = "database.ser";

    private final Map<String, User> users;
    private final Map<String, Map<String, String>> userDocuments; // username -> (docName -> content)

    /**
     * Constructor that initializes the database.
     * Attempts to load existing data from file if available.
     */
    public Database() {
        // Initialize with concurrent collections for thread safety
        users = new ConcurrentHashMap<>();
        userDocuments = new ConcurrentHashMap<>();

        // Try to load database from file
        loadFromFile();
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
        userDocuments.put(username, new ConcurrentHashMap<>());
        saveToFile();
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

    /**
     * Creates a new document for a user.
     *
     * @param username     The owner's username
     * @param documentName The name of the document
     * @return true if document was created, false if it already exists
     */
    public synchronized boolean createDocument(String username, String documentName) {
        Map<String, String> docs = userDocuments.get(username);
        if (docs == null) {
            docs = new ConcurrentHashMap<>();
            userDocuments.put(username, docs);
        }

        if (docs.containsKey(documentName)) {
            return false;
        }

        docs.put(documentName, ""); // Empty initial content
        saveToFile();
        return true;
    }

    /**
     * Gets the content of a user's document.
     *
     * @param username     The username
     * @param documentName The document name
     * @return The document content, or null if not found
     */
    public String getDocument(String username, String documentName) {
        Map<String, String> docs = userDocuments.get(username);
        return docs != null ? docs.get(documentName) : null;
    }

    /**
     * Updates a document's content.
     *
     * @param username     The username
     * @param documentName The document name
     * @param content      The new content
     * @return true if update was successful, false otherwise
     */
    public synchronized boolean updateDocument(String username, String documentName, String content) {
        Map<String, String> docs = userDocuments.get(username);
        if (docs == null || !docs.containsKey(documentName)) {
            return false;
        }

        docs.put(documentName, content);
        saveToFile();
        return true;
    }

    /**
     * Gets all documents for a user.
     *
     * @param username The username
     * @return A map of document names to contents
     */
    public Map<String, String> getUserDocuments(String username) {
        Map<String, String> docs = userDocuments.get(username);
        return docs != null ? new HashMap<>(docs) : new HashMap<>();
    }

    /**
     * Saves the database to a file.
     */
    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DB_FILE))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Error saving database: " + e.getMessage());
        }
    }

    /**
     * Loads the database from a file if it exists.
     */
    private void loadFromFile() {
        File file = new File(DB_FILE);
        if (!file.exists()) {
            // Add some default users for testing
            addUser("admin", "admin123");
            addUser("user1", "password1");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Database loadedDb = (Database) ois.readObject();

            // Copy data from loaded database
            users.putAll(loadedDb.users);
            userDocuments.putAll(loadedDb.userDocuments);

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading database: " + e.getMessage());
        }
    }
}
