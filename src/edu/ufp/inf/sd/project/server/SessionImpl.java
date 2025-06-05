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
    public void shareWithFileSystem(String username) throws RemoteException {
        database.addUserSharedFolder(this.username, username, fileSystem);
        createSharedFolderStructure(username, this.username);
    }

    @Override
    public void unshareWithFileSystem(String username) throws RemoteException {
        database.removeUserSharedFolder(this.username, username, this.fileSystem);
        removeSharedFolderStructure(username, this.username);
    }

    @Override
    public void synchronizeSharedFolders() throws RemoteException {
        Map<String, SubjectFileSystemRI> sharedFileSystems = database.getUserSharedFolders(this.username);

        if (sharedFileSystems != null) {
            for (String ownerUsername : sharedFileSystems.keySet()) {
                createSharedFolderStructure(this.username, ownerUsername);
                synchronizeFilesFromSharedUser(ownerUsername);
            }
        }
    }

    @Override
    public boolean logout() throws RemoteException {
        System.out.println("Logging out user: " + username);
        database.removeSession(username);
        return false;
    }

    private void createSharedFolderStructure(String targetUsername, String ownerUsername) throws RemoteException {
        try {
            String clientSharedPath = "/home/oshico/Projects/SD/data/" + targetUsername + "/shared/" + ownerUsername;
            java.io.File clientSharedDir = new java.io.File(clientSharedPath);
            if (!clientSharedDir.exists()) {
                clientSharedDir.mkdirs();
                System.out.println("Created client shared folder: " + clientSharedPath);
            }

            String serverSharedPath = "/home/oshico/Projects/SD/data/server/" + targetUsername + "/shared/" + ownerUsername;
            java.io.File serverSharedDir = new java.io.File(serverSharedPath);
            if (!serverSharedDir.exists()) {
                serverSharedDir.mkdirs();
                System.out.println("Created server shared folder: " + serverSharedPath);
            }
        } catch (Exception e) {
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
            throw new RemoteException("Failed to remove shared folder structure", e);
        }
    }

    private void synchronizeFilesFromSharedUser(String ownerUsername) throws RemoteException {
        try {
            String ownerPath = "/home/oshico/Projects/SD/data/server/" + ownerUsername;
            java.io.File ownerDir = new java.io.File(ownerPath);

            String sharedPath = "/home/oshico/Projects/SD/data/server/" + this.username + "/shared/" + ownerUsername;
            java.io.File sharedDir = new java.io.File(sharedPath);

            if (ownerDir.exists()) {
                copyDirectoryContents(ownerDir, sharedDir);
                System.out.println("Synchronized files from " + ownerUsername + " to " + this.username + "'s shared folder");
            }
        } catch (Exception e) {
            throw new RemoteException("Failed to synchronize shared files", e);
        }
    }

    private void copyDirectoryContents(java.io.File source, java.io.File destination) throws java.io.IOException {
        if (!destination.exists()) {
            destination.mkdirs();
        }

        java.io.File[] files = source.listFiles();
        if (files != null) {
            for (java.io.File file : files) {
                java.io.File destFile = new java.io.File(destination, file.getName());

                if (file.isDirectory()) {
                    copyDirectoryContents(file, destFile);
                } else {
                    try (java.io.FileInputStream fis = new java.io.FileInputStream(file);
                         java.io.FileOutputStream fos = new java.io.FileOutputStream(destFile)) {

                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                }
            }
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
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }

}
