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
    }

    @Override
    public void unshareWithFileSystem(String username) throws RemoteException {
        database.removeUserSharedFolder(this.username, username, this.fileSystem);
    }

    @Override
    public boolean logout() throws RemoteException {
        System.out.println("Logging out user: " + username);
        database.removeSession(username);
        return false;
    }


}
