package edu.ufp.inf.sd.project.main.user;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String username;
    private String passwordHash; // Store password hash, not plaintext
    private final List<String> ownedFolders; // Paths to folders owned by this user
    private final List<String> sharedFolders; // Paths to folders shared with this user

    /**
     * Creates a new user with the given username and password hash.
     *
     * @param username     The username for this user
     * @param passwordHash The hashed password for this user
     */
    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.ownedFolders = new ArrayList<>();
        this.sharedFolders = new ArrayList<>();
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public List<String> getOwnedFolders() {
        return ownedFolders;
    }

    public void addOwnedFolder(String folderPath) {
        if (!ownedFolders.contains(folderPath)) {
            ownedFolders.add(folderPath);
        }
    }

    public void removeOwnedFolder(String folderPath) {
        ownedFolders.remove(folderPath);
    }

    public List<String> getSharedFolders() {
        return sharedFolders;
    }

    public void addSharedFolder(String folderPath) {
        if (!sharedFolders.contains(folderPath)) {
            sharedFolders.add(folderPath);
        }
    }

    public void removeSharedFolder(String folderPath) {
        sharedFolders.remove(folderPath);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", ownedFolders=" + ownedFolders.size() +
                ", sharedFolders=" + sharedFolders.size() +
                '}';
    }
}
