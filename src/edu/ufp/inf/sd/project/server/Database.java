package edu.ufp.inf.sd.project.server;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Database implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final String DB_FILE = "database.ser";

    private final Map<String, User> users;
    private final Map<String, Map<String, String>> userDocuments; // username -> (docName -> content)
    private final Map<String, Map<String, Folder>> userFolders; // username -> (folderName -> folder)
    private final Map<String, Map<String, Set<String>>> userSharedFolders; // username -> (owner -> set of folderNames)

    /**
     * Constructor that initializes the database.
     * Attempts to load existing data from file if available.
     */
    public Database() {
        // Initialize with concurrent collections for thread safety
        users = new ConcurrentHashMap<>();
        userDocuments = new ConcurrentHashMap<>();
        userFolders = new ConcurrentHashMap<>();
        userSharedFolders = new ConcurrentHashMap<>();

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
        userFolders.put(username, new ConcurrentHashMap<>());
        userSharedFolders.put(username, new ConcurrentHashMap<>());
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
     * Creates a new folder for a user.
     *
     * @param username   The username
     * @param folderName The name of the folder
     * @return true if folder was created, false if it already exists
     */
    public synchronized boolean createFolder(String username, String folderName) {
        Map<String, Folder> folders = userFolders.get(username);
        if (folders == null) {
            folders = new ConcurrentHashMap<>();
            userFolders.put(username, folders);
        }

        if (folders.containsKey(folderName)) {
            return false;
        }

        Folder folder = new Folder(folderName, username);
        folders.put(folderName, folder);
        saveToFile();
        return true;
    }

    /**
     * Gets a folder by name for a specific user.
     *
     * @param username   The username
     * @param folderName The folder name
     * @return The Folder object, or null if not found
     */
    public Folder getFolder(String username, String folderName) {
        Map<String, Folder> folders = userFolders.get(username);
        return folders != null ? folders.get(folderName) : null;
    }

    /**
     * Gets all folders owned by a user.
     *
     * @param username The username
     * @return A map of folder names to Folder objects
     */
    public Map<String, Folder> getUserFolders(String username) {
        Map<String, Folder> folders = userFolders.get(username);
        return folders != null ? new HashMap<>(folders) : new HashMap<>();
    }

    /**
     * Adds a document to a folder.
     *
     * @param username     The username
     * @param folderName   The folder name
     * @param documentName The document name
     * @return true if document was added to the folder, false otherwise
     */
    public synchronized boolean addDocumentToFolder(String username, String folderName, String documentName) {
        Folder folder = getFolder(username, folderName);
        if (folder == null) {
            return false;
        }

        // Check if the document exists
        Map<String, String> docs = userDocuments.get(username);
        if (docs == null || !docs.containsKey(documentName)) {
            return false;
        }

        boolean added = folder.addDocument(documentName);
        if (added) {
            saveToFile();
        }
        return added;
    }

    /**
     * Removes a document from a folder.
     *
     * @param username     The username
     * @param folderName   The folder name
     * @param documentName The document name
     * @return true if document was removed from the folder, false otherwise
     */
    public synchronized boolean removeDocumentFromFolder(String username, String folderName, String documentName) {
        Folder folder = getFolder(username, folderName);
        if (folder == null) {
            return false;
        }

        boolean removed = folder.removeDocument(documentName);
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    /**
     * Shares a folder with another user.
     *
     * @param ownerUsername The owner's username
     * @param folderName    The folder name
     * @param username      The username to share with
     * @return true if folder was shared, false otherwise
     */
    public synchronized boolean shareFolder(String ownerUsername, String folderName, String username) {
        // Check if the target user exists
        if (!users.containsKey(username)) {
            return false;
        }

        // Check if the folder exists
        Folder folder = getFolder(ownerUsername, folderName);
        if (folder == null) {
            return false;
        }

        // Share the folder
        boolean shared = folder.shareWith(username);
        if (shared) {
            // Update the shared folders map for the target user
            Map<String, Set<String>> sharedFolders = userSharedFolders.get(username);
            if (sharedFolders == null) {
                sharedFolders = new ConcurrentHashMap<>();
                userSharedFolders.put(username, sharedFolders);
            }

            Set<String> ownerFolders = sharedFolders.get(ownerUsername);
            if (ownerFolders == null) {
                ownerFolders = new HashSet<>();
                sharedFolders.put(ownerUsername, ownerFolders);
            }

            ownerFolders.add(folderName);
            saveToFile();
        }

        return shared;
    }

    /**
     * Unshares a folder with a user.
     *
     * @param ownerUsername The owner's username
     * @param folderName    The folder name
     * @param username      The username to unshare with
     * @return true if folder was unshared, false otherwise
     */
    public synchronized boolean unshareFolder(String ownerUsername, String folderName, String username) {
        // Check if the folder exists
        Folder folder = getFolder(ownerUsername, folderName);
        if (folder == null) {
            return false;
        }

        // Unshare the folder
        boolean unshared = folder.unshareWith(username);
        if (unshared) {
            // Update the shared folders map for the target user
            Map<String, Set<String>> sharedFolders = userSharedFolders.get(username);
            if (sharedFolders != null) {
                Set<String> ownerFolders = sharedFolders.get(ownerUsername);
                if (ownerFolders != null) {
                    ownerFolders.remove(folderName);
                    if (ownerFolders.isEmpty()) {
                        sharedFolders.remove(ownerUsername);
                    }
                }
            }
            saveToFile();
        }

        return unshared;
    }

    /**
     * Gets all folders shared with a user.
     *
     * @param username The username
     * @return A map of owner usernames to maps of folder names to Folder objects
     */
    public Map<String, Map<String, Folder>> getSharedFoldersWithUser(String username) {
        Map<String, Map<String, Folder>> result = new HashMap<>();
        Map<String, Set<String>> sharedFolders = userSharedFolders.get(username);

        if (sharedFolders != null) {
            for (Map.Entry<String, Set<String>> entry : sharedFolders.entrySet()) {
                String ownerUsername = entry.getKey();
                Set<String> folderNames = entry.getValue();
                Map<String, Folder> ownerFolders = userFolders.get(ownerUsername);

                if (ownerFolders != null) {
                    Map<String, Folder> sharedOwnerFolders = new HashMap<>();
                    for (String folderName : folderNames) {
                        Folder folder = ownerFolders.get(folderName);
                        if (folder != null) {
                            sharedOwnerFolders.put(folderName, folder);
                        }
                    }
                    result.put(ownerUsername, sharedOwnerFolders);
                }
            }
        }

        return result;
    }

    /**
     * Gets all documents within a folder.
     *
     * @param ownerUsername The owner's username
     * @param folderName    The folder name
     * @return A map of document names to content
     */
    public Map<String, String> getFolderDocuments(String ownerUsername, String folderName) {
        Map<String, String> result = new HashMap<>();
        Folder folder = getFolder(ownerUsername, folderName);

        if (folder != null) {
            Map<String, String> ownerDocs = userDocuments.get(ownerUsername);
            if (ownerDocs != null) {
                for (String docName : folder.getDocuments()) {
                    String content = ownerDocs.get(docName);
                    if (content != null) {
                        result.put(docName, content);
                    }
                }
            }
        }

        return result;
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

            // Copy folder data if available in the loaded database
            if (loadedDb.userFolders != null) {
                userFolders.putAll(loadedDb.userFolders);
            }
            if (loadedDb.userSharedFolders != null) {
                userSharedFolders.putAll(loadedDb.userSharedFolders);
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading database: " + e.getMessage());
        }
    }
}
