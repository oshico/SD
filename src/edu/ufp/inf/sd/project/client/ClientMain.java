package edu.ufp.inf.sd.project.client;

import edu.ufp.inf.sd.project.server.AuthServiceRI;
import edu.ufp.inf.sd.project.util.SetupContextRMI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

public class ClientMain {
    private Registry registry;
    private AuthServiceRI authService;

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: java ClientMain <registry_host> <registry_port> <service_name>");
            System.exit(1);
        }

        String registryHost = args[0];
        String registryPort = args[1];
        String serviceName = args[2];

        ClientMain client = new ClientMain();
        client.lookupService(registryHost, registryPort, serviceName);
    }

    private void lookupService(String registryHost, String registryPort, String serviceName) {
        try {
            // Get the registry
            SetupContextRMI contextRMI = new SetupContextRMI(
                    this.getClass(), registryHost, registryPort, new String[]{serviceName});

            // Get the service URL
            String serviceUrl = contextRMI.getServicesUrl(0);
            System.out.println("Service URL: " + serviceUrl);

            // Get the registry
            registry = contextRMI.getRegistry();

            // Lookup service on registry
            authService = (AuthServiceRI) registry.lookup(serviceName);

            System.out.println("Found service " + serviceName + " at " + registryHost + ":" + registryPort);


        } catch (RemoteException | NotBoundException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            System.exit(1);
        }
    }
}
