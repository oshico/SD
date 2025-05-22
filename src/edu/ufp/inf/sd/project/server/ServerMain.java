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


    public ServerMain() {
        this.database = Database.getDatabaseInstance();
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
            AuthServiceRI authService = new AuthServiceImpl(database);

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
}
