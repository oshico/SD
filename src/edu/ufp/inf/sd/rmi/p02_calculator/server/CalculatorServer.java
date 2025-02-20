package edu.ufp.inf.sd.rmi.p02_calculator.server;

import edu.ufp.inf.sd.rmi.util.rmisetup.SetupContextRMI;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CalculatorServer {

    private SetupContextRMI contextRMI;
    private CalculatorRI calculatorRI;

    public static void main(String[] args) {
        if (args != null && args.length < 3) {
            System.err.println("usage: java [options] edu.ufp.inf.sd.rmi.p02_calculator.server.CalculatorServer <rmi_registry_ip> <rmi_registry_port> <service_name>");
            System.exit(-1);
        } else {
            // 1. Create and configure the server
            CalculatorServer server = new CalculatorServer(args);
            // 2. Bind the service in the RMI registry
            server.rebindService();
        }
    }

    public CalculatorServer(String[] args) {
        try {
            // Print arguments and initialize the context
            SetupContextRMI.printArgs(this.getClass().getName(), args);
            String registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];

            // Create RMI setup context
            contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});
        } catch (RemoteException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    private void rebindService() {
        try {
            // Get RMI registry proxy
            Registry registry = contextRMI.getRegistry();

            if (registry != null) {
                // Create the Calculator implementation
                calculatorRI = new CalculatorImpl();

                // Get the full service URL
                String serviceUrl = contextRMI.getServicesUrl(0);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Binding service at {0}", serviceUrl);

                // Bind the Calculator service to the registry
                registry.rebind(serviceUrl, calculatorRI);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Calculator service is running...");
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Registry is not available. Check IP and port.");
            }
        } catch (RemoteException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }
}
