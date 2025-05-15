package edu.ufp.inf.sd.project.server;

import java.io.Serial;
import java.io.Serializable;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String username;
    private final String passwordHash;

    /**
     * Constructor for a user with username and password hash.
     *
     * @param username     The username
     * @param passwordHash The hashed password
     */
    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    /**
     * Gets the username.
     *
     * @return The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password hash.
     *
     * @return The password hash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Validates the provided password against the stored hash.
     * In a real system, this should use proper password hashing.
     *
     * @param password The password to validate
     * @return true if the password is valid, false otherwise
     */
    public boolean validatePassword(String password) {
        // In a real system, use a proper password hashing algorithm
        // For this example, we're just comparing strings
        return passwordHash.equals(password);
    }
}
