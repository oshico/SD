package edu.ufp.inf.sd.rmi.util.rmisetup;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SetupContextRMI is responsible for setting up an RMI (Remote Method Invocation) context,
 * including network configuration, security management, and RMI registry handling.
 * This class initializes a registry and provides methods to retrieve service URLs
 * and check whether an RMI registry is running.
 *
 * @author rui
 */
public class SetupContextRMI {

    private Registry registry;
    private String localHostAddress;

    private final String[] serviceUrls;
    private final Logger logger;

    /**
     * Constructor that initializes the RMI setup context.
     *
     * @param subsystemClass The class representing the subsystem using this RMI context.
     * @param registryHostIP The IP address of the RMI registry.
     * @param registryHostPort The port number of the RMI registry.
     * @param serviceNames An array containing the names of the services to be registered.
     * @throws RemoteException If there is an issue with remote communication.
     */
    public SetupContextRMI(Class<?> subsystemClass, String registryHostIP, String registryHostPort, String[] serviceNames) throws RemoteException {
        this.logger = Logger.getLogger(subsystemClass.getName());

        logger.log(Level.INFO, "setup context for subsystemClass {0}", subsystemClass.getName());

        logger.log(Level.INFO, "serviceNames.length = {0}", serviceNames.length);

        String[] serviceNames1 = new String[serviceNames.length];
        System.arraycopy(serviceNames, 0, serviceNames1, 0, serviceNames.length);
        for (int i = 0; i < serviceNames.length; i++) {
            logger.log(Level.INFO, "serviceNames[{0}] = {1}", new Object[]{i, serviceNames[i]});
        }

        // 1. Set network context
        setupLocalHostInetAddress();

        String registryHostIP1;
        int registryHostPort1;
        if ((registryHostIP != null && registryHostPort != null)) {
            registryHostIP1 = registryHostIP;
            registryHostPort1 = Integer.parseInt(registryHostPort);
        } else {
            registryHostIP1 = this.localHostAddress;
            registryHostPort1 = 1099;
        }

        this.serviceUrls = new String[serviceNames1.length];
        logger.log(Level.INFO, "serviceUrls.length = {0}", this.serviceUrls.length);
        for (int i = 0; i < this.serviceUrls.length; i++) {
            serviceUrls[i] = "rmi://" + registryHostIP1 + ":" + registryHostPort1 + "/" + serviceNames1[i];
            logger.log(Level.INFO, "serviceUrls[{0}] = {1}", new Object[]{i, serviceUrls[i]});
        }

        // 2. Set and list registry context
        setupRegistryContext(registryHostIP1, registryHostPort1);
    }

    /**
     * Retrieves the service URL for the given index.
     *
     * @param i The index of the service URL.
     * @return The service URL if index is valid, otherwise null.
     */
    public String getServicesUrl(int i) {
        return (i < this.serviceUrls.length ? serviceUrls[i] : null);
    }

    /**
     * Sets up the local host address and network context.
     */
    private void setupLocalHostInetAddress() {
        try {
            InetAddress localHostInetAddress = InetAddress.getLocalHost();
            String localHostName = localHostInetAddress.getHostName();
            localHostAddress = localHostInetAddress.getHostAddress();

            logger.log(Level.INFO, "localHostName = {0}", new Object[]{localHostName});
            logger.log(Level.INFO, "localHostAddress = {0}", new Object[]{localHostAddress});

            InetAddress[] allLocalInetAddresses = InetAddress.getAllByName(localHostName);
            for (InetAddress addr : allLocalInetAddresses) {
                logger.log(Level.INFO, "addr = {0}", new Object[]{addr});
            }
        } catch (UnknownHostException e) {
            logger.log(Level.SEVERE, "exception {0}", new Object[]{e});
        }
    }

    /**
     * Returns the current RMI registry proxy.
     *
     * @return The RMI registry instance.
     */
    public Registry getRegistry() {
        return this.registry;
    }

    /**
     * Sets up reference for the RMI registry and lists available services.
     *
     * @param registryHostIP The IP address of the RMI registry.
     * @param registryHostPort The port number of the RMI registry.
     * @throws RemoteException If an error occurs while setting up the registry context.
     */
    private void setupRegistryContext(String registryHostIP, int registryHostPort) throws RemoteException {
        if (isRMIRegistryRunning(registryHostIP, registryHostPort)) {
            registry = LocateRegistry.getRegistry(registryHostIP, registryHostPort);
        } else {
            registry = LocateRegistry.createRegistry(registryHostPort);
        }
        logger.log(Level.INFO, "Embedded RMI Registry started on port {0}...", new Object[]{registryHostPort});

        if (registry != null) {
            String[] registriesList = registry.list();
            logger.log(Level.INFO, "registriesList.length = {0}", new Object[]{registriesList.length});

            for (int i = 0; i < registriesList.length; i++) {
                logger.log(Level.INFO, "registriesList[{0}] = {1}", new Object[]{i, registriesList[i]});
            }
        } else {
            logger.log(Level.INFO, "Reference to registry is null!!");
        }
    }

    /**
     * Prints the provided command-line arguments.
     *
     * @param classname The name of the class executing the method.
     * @param args The array of arguments to print.
     */
    public static void printArgs(String classname, String[] args) {
        for (int i = 0; args != null && i < args.length; i++) {
            Logger.getLogger(classname).log(Level.INFO, "args[{0}] = {1}", new Object[]{i, args[i]});
        }
    }

    /**
     * Checks if an RMI registry is running on the specified host and port.
     *
     * @param host The host where the RMI registry is expected to be running.
     * @param port The port number of the RMI registry.
     * @return true if the registry is running, false otherwise.
     */
    public static boolean isRMIRegistryRunning(String host, int port) {
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            registry.list(); // Attempt to list bound services
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
