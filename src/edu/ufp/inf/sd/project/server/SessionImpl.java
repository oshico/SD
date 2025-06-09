package edu.ufp.inf.sd.project.server;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class SessionImpl extends UnicastRemoteObject implements SessionRI {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String username;
    private final String sessionId;
    private final Database database;
    private final SubjectFileSystemRI fileSystem;

    public SessionImpl(String username, Database database) throws RemoteException {
        super();
        this.username = username;
        this.sessionId = UUID.randomUUID().toString();
        this.database = database;
        this.fileSystem = new SubjectFileSystemImpl(username, database);
        System.out.println("Created new session for user: " + username + " with ID: " + sessionId);
    }

    @Override
    public String getUsername() throws RemoteException {
        return username;
    }

    @Override
    public SubjectFileSystemRI getFileSystem() throws RemoteException {
        return fileSystem;
    }

    @Override
    public Map<String, SubjectFileSystemRI> getSharedWithMeFileSystem() throws RemoteException {
        return database.getUserSharedFolders(this.username);
    }

    @Override
    public void shareWithFileSystem(String targetUsername) throws RemoteException {
        database.addUserSharedFolder(targetUsername, this.username, this.fileSystem);
        createSharedFolderStructure(targetUsername, this.username);
        synchronizeSpecificUserFiles(targetUsername, this.username);
    }

    @Override
    public void unshareWithFileSystem(String targetUsername) throws RemoteException {
        database.removeUserSharedFolder(this.username, targetUsername, this.fileSystem);
        removeSharedFolderStructure(targetUsername, this.username);
    }

    @Override
    public void synchronizeSharedFolders() throws RemoteException {
        Map<String, SubjectFileSystemRI> sharedFileSystems = database.getUserSharedFolders(this.username);

        if (sharedFileSystems != null && !sharedFileSystems.isEmpty()) {
            System.out.println("Synchronizing shared folders for user: " + this.username);
            System.out.println("Found " + sharedFileSystems.size() + " shared file systems");

            for (String ownerUsername : sharedFileSystems.keySet()) {
                System.out.println("Synchronizing files from owner: " + ownerUsername);
                createSharedFolderStructure(this.username, ownerUsername);
                synchronizeFilesFromSharedUser(ownerUsername);
            }
        } else {
            System.out.println("No shared file systems found for user: " + this.username);
        }
    }

    @Override
    public boolean logout() throws RemoteException {
        System.out.println("Logging out user: " + username);
        database.removeSession(username);
        return true; // Changed to true for successful logout
    }

    private void createSharedFolderStructure(String targetUsername, String ownerUsername) throws RemoteException {
        try {
            // Create client-side shared folder structure
            String clientSharedPath = "/home/oshico/Projects/SD/data/" + targetUsername + "/shared/" + ownerUsername;
            java.io.File clientSharedDir = new java.io.File(clientSharedPath);
            if (!clientSharedDir.exists()) {
                boolean created = clientSharedDir.mkdirs();
                System.out.println("Created client shared folder: " + clientSharedPath + " - Success: " + created);
            }

            // Create server-side shared folder structure
            String serverSharedPath = "/home/oshico/Projects/SD/data/server/" + targetUsername + "/shared/" + ownerUsername;
            java.io.File serverSharedDir = new java.io.File(serverSharedPath);
            if (!serverSharedDir.exists()) {
                boolean created = serverSharedDir.mkdirs();
                System.out.println("Created server shared folder: " + serverSharedPath + " - Success: " + created);
            }
        } catch (Exception e) {
            System.err.println("Failed to create shared folder structure: " + e.getMessage());
            throw new RemoteException("Failed to create shared folder structure", e);
        }
    }

    private void removeSharedFolderStructure(String targetUsername, String ownerUsername) throws RemoteException {
        try {
            String clientSharedPath = "/home/oshico/Projects/SD/data/" + targetUsername + "/shared/" + ownerUsername;
            java.io.File clientSharedDir = new java.io.File(clientSharedPath);
            deleteDirectoryRecursively(clientSharedDir);

            String serverSharedPath = "/home/oshico/Projects/SD/data/server/" + targetUsername + "/shared/" + ownerUsername;
            java.io.File serverSharedDir = new java.io.File(serverSharedPath);
            deleteDirectoryRecursively(serverSharedDir);

            System.out.println("Removed shared folder structure for " + ownerUsername + " from " + targetUsername);
        } catch (Exception e) {
            System.err.println("Failed to remove shared folder structure: " + e.getMessage());
            throw new RemoteException("Failed to remove shared folder structure", e);
        }
    }

    private void synchronizeFilesFromSharedUser(String ownerUsername) throws RemoteException {
        try {
            // Source: Owner's server directory
            String ownerServerPath = "/home/oshico/Projects/SD/data/server/" + ownerUsername;
            java.io.File ownerServerDir = new java.io.File(ownerServerPath);

            // Destination 1: Current user's server shared folder
            String sharedServerPath = "/home/oshico/Projects/SD/data/server/" + this.username + "/shared/" + ownerUsername;
            java.io.File sharedServerDir = new java.io.File(sharedServerPath);

            // Destination 2: Current user's client shared folder
            String sharedClientPath = "/home/oshico/Projects/SD/data/" + this.username + "/shared/" + ownerUsername;
            java.io.File sharedClientDir = new java.io.File(sharedClientPath);

            System.out.println("Synchronizing from: " + ownerServerPath);
            System.out.println("To server shared: " + sharedServerPath);
            System.out.println("To client shared: " + sharedClientPath);

            if (ownerServerDir.exists() && ownerServerDir.isDirectory()) {
                // Ensure destination directories exist
                if (!sharedServerDir.exists()) {
                    sharedServerDir.mkdirs();
                }
                if (!sharedClientDir.exists()) {
                    sharedClientDir.mkdirs();
                }

                copyDirectoryContents(ownerServerDir, sharedServerDir);
                copyDirectoryContents(ownerServerDir, sharedClientDir);

                System.out.println("Successfully synchronized files from " + ownerUsername + " to " + this.username + "'s shared folders");
            } else {
                System.out.println("Owner directory does not exist or is not a directory: " + ownerServerPath);
            }
        } catch (Exception e) {
            System.err.println("Failed to synchronize shared files: " + e.getMessage());
            e.printStackTrace();
            throw new RemoteException("Failed to synchronize shared files", e);
        }
    }

    // New method for immediate synchronization when sharing
    private void synchronizeSpecificUserFiles(String targetUsername, String ownerUsername) throws RemoteException {
        try {
            // Source: Owner's server directory (this user's files)
            String ownerServerPath = "/home/oshico/Projects/SD/data/server/" + ownerUsername;
            java.io.File ownerServerDir = new java.io.File(ownerServerPath);

            // Destination 1: Target user's server shared folder
            String sharedServerPath = "/home/oshico/Projects/SD/data/server/" + targetUsername + "/shared/" + ownerUsername;
            java.io.File sharedServerDir = new java.io.File(sharedServerPath);

            // Destination 2: Target user's client shared folder
            String sharedClientPath = "/home/oshico/Projects/SD/data/" + targetUsername + "/shared/" + ownerUsername;
            java.io.File sharedClientDir = new java.io.File(sharedClientPath);

            System.out.println("Immediate sync from: " + ownerServerPath);
            System.out.println("To target server shared: " + sharedServerPath);
            System.out.println("To target client shared: " + sharedClientPath);

            if (ownerServerDir.exists() && ownerServerDir.isDirectory()) {
                // Ensure destination directories exist
                if (!sharedServerDir.exists()) {
                    sharedServerDir.mkdirs();
                }
                if (!sharedClientDir.exists()) {
                    sharedClientDir.mkdirs();
                }

                // Copy to both server and client shared directories
                copyDirectoryContents(ownerServerDir, sharedServerDir);
                copyDirectoryContents(ownerServerDir, sharedClientDir);

                System.out.println("Successfully shared files from " + ownerUsername + " to " + targetUsername);
            } else {
                System.out.println("Owner directory does not exist: " + ownerServerPath);
            }
        } catch (Exception e) {
            System.err.println("Failed to sync files immediately: " + e.getMessage());
            e.printStackTrace();
            throw new RemoteException("Failed to sync files immediately", e);
        }
    }

    private void copyDirectoryContents(java.io.File source, java.io.File destination) throws java.io.IOException {
        System.out.println("Copying from: " + source.getAbsolutePath() + " to: " + destination.getAbsolutePath());

        if (!destination.exists()) {
            boolean created = destination.mkdirs();
            System.out.println("Created destination directory: " + created);
        }

        java.io.File[] files = source.listFiles();
        if (files != null && files.length > 0) {
            System.out.println("Found " + files.length + " items to copy");

            for (java.io.File file : files) {
                java.io.File destFile = new java.io.File(destination, file.getName());
                System.out.println("Processing: " + file.getName() + " (isDirectory: " + file.isDirectory() + ")");

                if (file.isDirectory()) {
                    // Recursively copy directory
                    copyDirectoryContents(file, destFile);
                } else {
                    // Copy file
                    try (java.io.FileInputStream fis = new java.io.FileInputStream(file);
                         java.io.FileOutputStream fos = new java.io.FileOutputStream(destFile)) {

                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        long totalBytes = 0;

                        while ((bytesRead = fis.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                            totalBytes += bytesRead;
                        }

                        System.out.println("Copied file: " + file.getName() + " (" + totalBytes + " bytes)");
                    } catch (java.io.IOException e) {
                        System.err.println("Failed to copy file: " + file.getName() + " - " + e.getMessage());
                        throw e;
                    }
                }
            }
        } else {
            System.out.println("Source directory is empty or null: " + source.getAbsolutePath());
        }
    }

    /**
     * Recursively deletes a directory and its contents
     */
    private void deleteDirectoryRecursively(java.io.File directory) {
        if (directory.exists()) {
            java.io.File[] files = directory.listFiles();
            if (files != null) {
                for (java.io.File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectoryRecursively(file);
                    } else {
                        boolean deleted = file.delete();
                        System.out.println("Deleted file: " + file.getName() + " - Success: " + deleted);
                    }
                }
            }
            boolean deleted = directory.delete();
            System.out.println("Deleted directory: " + directory.getName() + " - Success: " + deleted);
        }
    }
}