package edu.ufp.inf.sd.project.main.filesystem;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface FileSystemRI extends Remote {
    /**
     * Lists all items in a folder.
     *
     * @param sessionId  The session ID of the authenticated user
     * @param folderPath The path of the folder
     * @return A list of items in the folder
     * @throws RemoteException If a remote communication error occurs
     */
    List<FileSystemItem> listFolder(String sessionId, String folderPath) throws RemoteException;

    /**
     * Creates a new folder.
     *
     * @param sessionId  The session ID of the authenticated user
     * @param parentPath The path of the parent folder
     * @param folderName The name of the new folder
     * @return True if the folder was created, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    boolean createFolder(String sessionId, String parentPath, String folderName) throws RemoteException;

    /**
     * Deletes a folder.
     *
     * @param sessionId  The session ID of the authenticated user
     * @param folderPath The path of the folder to delete
     * @return True if the folder was deleted, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    boolean deleteFolder(String sessionId, String folderPath) throws RemoteException;

    /**
     * Renames a folder.
     *
     * @param sessionId  The session ID of the authenticated user
     * @param folderPath The path of the folder to rename
     * @param newName    The new name for the folder
     * @return True if the folder was renamed, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    boolean renameFolder(String sessionId, String folderPath, String newName) throws RemoteException;

    /**
     * Moves a folder to a new location.
     *
     * @param sessionId     The session ID of the authenticated user
     * @param folderPath    The path of the folder to move
     * @param newParentPath The path of the new parent folder
     * @return True if the folder was moved, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    boolean moveFolder(String sessionId, String folderPath, String newParentPath) throws RemoteException;

    /**
     * Uploads a file to a folder.
     *
     * @param sessionId   The session ID of the authenticated user
     * @param parentPath  The path of the parent folder
     * @param fileName    The name of the file
     * @param content     The content of the file
     * @param contentType The MIME type of the file
     * @return True if the file was uploaded, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    boolean uploadFile(String sessionId, String parentPath, String fileName, byte[] content, String contentType) throws RemoteException;

    /**
     * Downloads a file.
     *
     * @param sessionId The session ID of the authenticated user
     * @param filePath  The path of the file to download
     * @return The file, or null if not found or not accessible
     * @throws RemoteException If a remote communication error occurs
     */
    File downloadFile(String sessionId, String filePath) throws RemoteException;

    /**
     * Deletes a file.
     *
     * @param sessionId The session ID of the authenticated user
     * @param filePath  The path of the file to delete
     * @return True if the file was deleted, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    boolean deleteFile(String sessionId, String filePath) throws RemoteException;

    /**
     * Renames a file.
     *
     * @param sessionId The session ID of the authenticated user
     * @param filePath  The path of the file to rename
     * @param newName   The new name for the file
     * @return True if the file was renamed, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    boolean renameFile(String sessionId, String filePath, String newName) throws RemoteException;

    /**
     * Moves a file to a new location.
     *
     * @param sessionId     The session ID of the authenticated user
     * @param filePath      The path of the file to move
     * @param newParentPath The path of the new parent folder
     * @return True if the file was moved, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    boolean moveFile(String sessionId, String filePath, String newParentPath) throws RemoteException;

    /**
     * Shares a folder with another user.
     *
     * @param sessionId      The session ID of the authenticated user
     * @param folderPath     The path of the folder to share
     * @param targetUsername The username of the user to share with
     * @return True if the folder was shared, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    boolean shareFolder(String sessionId, String folderPath, String targetUsername) throws RemoteException;

    /**
     * Unshares a folder from another user.
     *
     * @param sessionId      The session ID of the authenticated user
     * @param folderPath     The path of the folder to unshare
     * @param targetUsername The username of the user to unshare from
     * @return True if the folder was unshared, false otherwise
     * @throws RemoteException If a remote communication error occurs
     */
    boolean unshareFolder(String sessionId, String folderPath, String targetUsername) throws RemoteException;

    /**
     * Gets all folders shared with the authenticated user.
     *
     * @param sessionId The session ID of the authenticated user
     * @return A list of shared folders
     * @throws RemoteException If a remote communication error occurs
     */
    List<Folder> getSharedFolders(String sessionId) throws RemoteException;
}
