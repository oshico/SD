package edu.ufp.inf.sd.rmi.p03_pingpong.client;

import edu.ufp.inf.sd.rmi.p03_pingpong.server.PingImpl;
import edu.ufp.inf.sd.rmi.p03_pingpong.server.PingRI;
import edu.ufp.inf.sd.rmi.p03_pingpong.server.Ball;
import edu.ufp.inf.sd.rmi.util.rmisetup.SetupContextRMI;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PingpongClient {

    /**
     * Context for connecting a RMI client to a RMI Servant
     */
    private SetupContextRMI contextRMI;
    /**
     * Remote interface that will hold the Servant proxy
     */
    private PingRI pingRI;
    Integer playerId;

    public static void main(String[] args) {
        if (args != null && args.length < 3) {
            System.err.println("usage: java [options] edu.ufp.sd.inf.rmi.p02_calculator.server.HelloWorldClient <rmi_registry_ip> <rmi_registry_port> <service_name> <player id>");
            System.exit(-1);
        } else {
            //1. ============ Setup client RMI context ============
            PingpongClient ppc = new PingpongClient(args);
            //2. ============ Lookup service ============
            ppc.lookupService();
            //3. ============ Play with service ============
            ppc.playService();
        }
    }

    public PingpongClient(String[] args) {
        try {
            //List ans set args
            SetupContextRMI.printArgs(this.getClass().getName(), args);
            String registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];
            playerId = Integer.parseInt(args[3]);
            //Create a context for RMI setup
            contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});
        } catch (RemoteException e) {
            Logger.getLogger(PingpongClient.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private Remote lookupService() {
        try {
            //Get proxy to rmiregistry
            Registry registry = contextRMI.getRegistry();
            //Lookup service on rmiregistry and wait for calls
            if (registry != null) {
                //Get service url (including servicename)
                String serviceUrl = contextRMI.getServicesUrl(0);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going to lookup service @ {0}", serviceUrl);

                //============ Get proxy to HelloWorld service ============
                pingRI = (PingRI) registry.lookup(serviceUrl);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
                //registry = LocateRegistry.createRegistry(1099);
            }
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return pingRI;
    }

    private void playService() {
        try {
            //============ Call HelloWorld remote service ============
            Ball b = new Ball(this.playerId);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Client sending ball to server");
            PongImpl pongRI = new PongImpl(pingRI, b);
            pongRI.startPlaying();
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going to finish, bye. ;)");
        } catch (RemoteException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
}
