/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * <p>
 * Title: Projecto SD</p>
 * <p>
 * Description: Projecto apoio aulas SD</p>
 * <p>
 * Copyright: Copyright (c) 2011</p>
 * <p>
 * Company: UFP </p>
 *
 * @author Rui Moreira
 * @version 2.0
 */
package edu.ufp.inf.sd.rmi.p05_observer.server;

import edu.ufp.inf.sd.rmi.p04_diglib.server.DigLibFactoryImpl;
import edu.ufp.inf.sd.rmi.p04_diglib.server.DigLibFactoryRI;
import edu.ufp.inf.sd.rmi.p04_diglib.server.DigLibFactoryServer;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rjm
 */
public class SubjectServer {

    public static void main(String args[]) {
        //0. Get a Logger for debug messages
        Logger logger = Logger.getLogger(SubjectServer.class.getName());

        try {
            //1. ============ Set default RMI parameters ============
            String registryIP = InetAddress.getLocalHost().getHostAddress();
            int registryPort = 1099;
            String serviceName = "ObserverService";

            logger.log(Level.INFO, "main(): args.length = " + args.length);

            //2. ============ Update RMI parameters when received through main args ============
            if (args.length == 3) {
                registryIP = args[0];
                registryPort = Integer.parseInt(args[1]);
                serviceName = args[2];
            } else {
                System.err.println("usage: java [options] edu.ufp.inf.sd.rmi.p05_observer.server.SubjectServer <server_rmi_hostname/ip>  <server_rmi_port> <service_name>");
                System.exit(1);
            }

            //3. ============ Create Local RMIRegistry service for publishing the service ============
            logger.log(Level.INFO, "main(): going to create RMIRegister on rmiregistryPort = " + registryPort + "...");
            Registry registry = LocateRegistry.createRegistry(registryPort);

            //4. ============ Create service/servant ============
            logger.log(Level.INFO, "main(): going to create service...");
            SubjectRI subRI = (SubjectRI) new SubjectImpl();

            //5. ============ Rebind servant into RMIRegistry witgh given service url ============
            String serviceUrl = "rmi://"+registryIP+":"+registryPort+"/"+serviceName;
            logger.log(Level.INFO, "main(): going to register service on " + serviceUrl + "...");
            registry.rebind(serviceUrl, subRI);
            logger.log(Level.INFO, "main(): service bound and running. :)");

            logger.log(Level.INFO, "RMI Server running at java.rmi.server.hostname: " + System.getProperty("java.rmi.server.hostname"));
        } catch (NumberFormatException | RemoteException | UnknownHostException e) {
            Logger.getLogger(SubjectServer.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
