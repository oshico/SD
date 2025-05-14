package edu.ufp.inf.sd.project.main.user;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class UserSession implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String sessionId;
    private final String username;
    private final Date creationTime;
    private Date lastAccessTime;
    private boolean active;

    /**
     * Creates a new user session for the given username.
     *
     * @param username The username for this session
     */
    public UserSession(String username) {
        this.sessionId = UUID.randomUUID().toString();
        this.username = username;
        this.creationTime = new Date();
        this.lastAccessTime = new Date();
        this.active = true;
    }

    /**
     * Updates the last access time to the current time.
     */
    public void updateLastAccessTime() {
        this.lastAccessTime = new Date();
    }

    /**
     * Invalidates this session.
     */
    public void invalidate() {
        this.active = false;
    }

    /**
     * Checks if this session has expired.
     *
     * @param timeoutMillis The session timeout in milliseconds
     * @return True if the session has expired, false otherwise
     */
    public boolean isExpired(long timeoutMillis) {
        if (!active) return true;
        long elapsed = new Date().getTime() - lastAccessTime.getTime();
        return elapsed > timeoutMillis;
    }

    // Getters
    public String getSessionId() {
        return sessionId;
    }

    public String getUsername() {
        return username;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserSession session = (UserSession) obj;
        return sessionId.equals(session.sessionId);
    }

    @Override
    public int hashCode() {
        return sessionId.hashCode();
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "sessionId='" + sessionId + '\'' +
                ", username='" + username + '\'' +
                ", creationTime=" + creationTime +
                ", lastAccessTime=" + lastAccessTime +
                ", active=" + active +
                '}';
    }

}
