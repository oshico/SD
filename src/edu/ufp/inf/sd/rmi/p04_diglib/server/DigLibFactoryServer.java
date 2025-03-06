package edu.ufp.inf.sd.rmi.p04_diglib.server;

import edu.ufp.inf.sd.rmi.p04_diglib.client.DigLibClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * Title: Projecto SD</p>
 * <p>
 * Description: Projecto apoio aulas SD</p>
 * <p>
 * Copyright: Copyright (c) 2017</p>
 * <p>
 * Company: UFP </p>
 *
 * @author Rui S. Moreira
 * @version 3.0
 */
public class DigLibFactoryServer {

    public static void main(String[] args) {
        Logger logger = Logger.getLogger(DigLibFactoryServer.class.getName());
        try {
            String rmiregistryIP = InetAddress.getLocalHost().getHostAddress();
            int rmiregistryPort = 1099;
            String rmiServiceName = "DigLibService";

            logger.log(Level.INFO, "main(): args.length = " + args.length);

            if (args.length == 3) {
                rmiregistryIP = args[0];
                rmiregistryPort = Integer.parseInt(args[1]);
                rmiServiceName = args[2];
            } else {
                System.err.println("usage: java [options] edu.ufp.sd.p04_diglib.server.DigLibFactoryServer <server_rmi_hostname/ip>  <server_rmi_port> <service_name>");
                System.exit(1);
            }

            //Creqate Local RMIRegistry service for publishing the service
            logger.log(Level.INFO, "main(): going to create RMIRegister on rmiregistryPort = " + rmiregistryPort + "...");
            Registry registry = LocateRegistry.createRegistry(rmiregistryPort);

            //============ Create service/servant ============
            logger.log(Level.INFO, "main(): going to create service...");
            DigLibFactoryRI diglibFactoryRI = new DigLibFactoryImpl();

            //============ Rebind servant ============
            String serviceUrl = "rmi://"+rmiregistryIP+":"+rmiregistryPort+"/"+rmiServiceName;
            logger.log(Level.INFO, "main(): going to register service on " + serviceUrl + "...");
            registry.rebind(serviceUrl, diglibFactoryRI);
            logger.log(Level.INFO, "main(): service bound and running. :)");

            System.out.println("RMI Server running at: " + System.getProperty("java.rmi.server.hostname"));

        } catch (UnknownHostException | RemoteException | NumberFormatException e) {
            logger.log(Level.SEVERE, null, e);
        }
    }
}
