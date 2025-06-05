package edu.ufp.inf.sd.project.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

import edu.ufp.inf.sd.project.client.ObserverRI;

public interface SessionRI extends Remote {

    String getUsername() throws RemoteException;

    SubjectFileSystemRI getFileSystem() throws RemoteException;

    Map<String,SubjectFileSystemRI> getSharedWithMeFileSystem() throws RemoteException;

    void shareWithFileSystem(String username) throws RemoteException;

    void unshareWithFileSystem(String username) throws RemoteException;

    void synchronizeSharedFolders() throws RemoteException;

    boolean logout() throws RemoteException;
}
