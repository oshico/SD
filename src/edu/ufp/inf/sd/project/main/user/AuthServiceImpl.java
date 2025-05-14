package edu.ufp.inf.sd.project.main.user;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthServiceImpl extends UnicastRemoteObject implements AuthService {
    private static final long serialVersionUID = 1L;

    // Session timeout in milliseconds (30 minutes)
    private static final long SESSION_TIMEOUT = 30 * 60 * 1000;

    // Maps to store users and active sessions
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, UserSession> sessions = new ConcurrentHashMap<>();

    /**
     * Creates a new authentication service.
     */
    public AuthServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public boolean registerUser(String username, String password) throws RemoteException {
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }

        // Check if username already exists
        if (users.containsKey(username)) {
            return false;
        }

        // Hash the password
        String passwordHash = hashPassword(password);
        if (passwordHash == null) {
            return false;
        }

        // Create and store the new user
        User newUser = new User(username, passwordHash);
        users.put(username, newUser);

        System.out.println("User registered: " + username);
        return true;
    }

    @Override
    public String login(String username, String password) throws RemoteException {
        if (username == null || password == null) {
            return null;
        }

        // Get the user
        User user = users.get(username);
        if (user == null) {
            return null;
        }

        // Verify the password
        String passwordHash = hashPassword(password);
        if (passwordHash == null || !passwordHash.equals(user.getPasswordHash())) {
            return null;
        }

        // Create a new session
        UserSession session = new UserSession(username);
        sessions.put(session.getSessionId(), session);

        System.out.println("User logged in: " + username);
        return session.getSessionId();
    }

    @Override
    public String validateSession(String sessionId) throws RemoteException {
        if (sessionId == null) {
            return null;
        }

        // Get the session
        UserSession session = sessions.get(sessionId);
        if (session == null || session.isExpired(SESSION_TIMEOUT)) {
            // Session not found or expired
            if (session != null) {
                sessions.remove(sessionId);
            }
            return null;
        }

        // Update last access time
        session.updateLastAccessTime();
        return session.getUsername();
    }

    @Override
    public boolean logout(String sessionId) throws RemoteException {
        if (sessionId == null) {
            return false;
        }

        // Get the session
        UserSession session = sessions.get(sessionId);
        if (session == null) {
            return false;
        }

        // Invalidate and remove the session
        session.invalidate();
        sessions.remove(sessionId);

        System.out.println("User logged out: " + session.getUsername());
        return true;
    }

    @Override
    public boolean changePassword(String sessionId, String oldPassword, String newPassword) throws RemoteException {
        if (sessionId == null || oldPassword == null || newPassword == null) {
            return false;
        }

        // Validate the session
        String username = validateSession(sessionId);
        if (username == null) {
            return false;
        }

        // Get the user
        User user = users.get(username);
        if (user == null) {
            return false;
        }

        // Verify the old password
        String oldPasswordHash = hashPassword(oldPassword);
        if (oldPasswordHash == null || !oldPasswordHash.equals(user.getPasswordHash())) {
            return false;
        }

        // Hash and set the new password
        String newPasswordHash = hashPassword(newPassword);
        if (newPasswordHash == null) {
            return false;
        }

        user.setPasswordHash(newPasswordHash);
        System.out.println("Password changed for user: " + username);
        return true;
    }

    /**
     * Hashes a password using SHA-256.
     *
     * @param password The password to hash
     * @return The hashed password as a hex string
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());

            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets a user by username.
     *
     * @param username The username to look up
     * @return The user, or null if not found
     */
    public User getUser(String username) {
        return users.get(username);
    }

    /**
     * Gets a session by session ID.
     *
     * @param sessionId The session ID to look up
     * @return The session, or null if not found
     */
    public UserSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    /**
     * Cleanup expired sessions.
     */
    public void cleanupExpiredSessions() {
        sessions.entrySet().removeIf(entry -> entry.getValue().isExpired(SESSION_TIMEOUT));
    }
}
