package edu.ufp.inf.sd.project.server;

import edu.ufp.inf.sd.project.util.SetupContextRMI;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerMain {
    private static final String SERVICE_NAME = "CollaborativeEditor";

    private final Database database;
    private final Map<String, SessionImpl> activeSessions;

    /**
     * Constructor for the server main class.
     */
    public ServerMain() {
        this.database = new Database();
        this.activeSessions = new ConcurrentHashMap<>();
    }

    /**
     * Main method to start the server.
     *
     * @param args Command line arguments (registry host, registry port, service name)
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: java ServerMain <registry_host> <registry_port> <service_name>");
            System.exit(1);
        }

        String registryHost = args[0];
        String registryPort = args[1];
        String serviceName = args[2];

        ServerMain server = new ServerMain();
        server.rebindService(registryHost, registryPort, serviceName);
    }

    /**
     * Rebinds the service to the RMI registry.
     *
     * @param registryHost The registry host
     * @param registryPort The registry port
     * @param serviceName  The service name
     */
    private void rebindService(String registryHost, String registryPort, String serviceName) {
        try {
            System.out.println("Starting server...");

            // Setup RMI context
            SetupContextRMI contextRMI = new SetupContextRMI(this.getClass(), registryHost, registryPort, new String[]{serviceName});

            // Create and bind the authentication service
            AuthServiceRI authService = new AuthServiceImpl(this, database);

            // Get registry
            Registry registry = contextRMI.getRegistry();

            // Rebind authentication service to registry
            registry.rebind(serviceName, authService);

            System.out.println("Server running on " + registryHost + ":" + registryPort);
            System.out.println("Service '" + serviceName + "' registered");

        } catch (RemoteException e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets the database instance.
     *
     * @return The database instance
     */
    public Database getDatabase() {
        return database;
    }

    /**
     * Adds an active session to the server.
     *
     * @param username The username
     * @param session  The session
     */
    public void addActiveSession(String username, SessionImpl session) {
        // If user already has a session, invalidate it
        SessionImpl existingSession = activeSessions.get(username);
        if (existingSession != null) {
            existingSession.invalidate();
        }

        activeSessions.put(username, session);
        System.out.println("Active sessions: " + activeSessions.size());
    }

    /**
     * Removes an active session from the server.
     *
     * @param username The username
     */
    public void removeActiveSession(String username) {
        activeSessions.remove(username);
        System.out.println("Session removed for user: " + username);
        System.out.println("Active sessions: " + activeSessions.size());
    }

    /**
     * Gets all active sessions.
     *
     * @return A map of usernames to sessions
     */
    public Map<String, SessionImpl> getActiveSessions() {
        return new HashMap<>(activeSessions);
    }

    /**
     * Gets an active session by username.
     *
     * @param username The username
     * @return The session, or null if not found
     */
    public SessionImpl getSession(String username) {
        return activeSessions.get(username);
    }
}
