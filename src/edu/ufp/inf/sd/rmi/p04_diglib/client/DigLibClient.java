package edu.ufp.inf.sd.rmi.p04_diglib.client;

import edu.ufp.inf.sd.rmi.p04_diglib.server.Book;
import edu.ufp.inf.sd.rmi.p04_diglib.server.DigLibFactoryRI;
import edu.ufp.inf.sd.rmi.p04_diglib.server.DigLibSessionRI;
import edu.ufp.inf.sd.rmi.p04_diglib.server.User;
import edu.ufp.inf.sd.rmi.util.rmisetup.SetupContextRMI;

import java.rmi.NotBoundException;
import java.rmi.Remote;
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
public class DigLibClient {

    public static void main(String[] args) {
        Logger logger = Logger.getLogger(DigLibClient.class.getName());
        if (args != null && args.length < 2) {
            System.err.println("usage: java [options] edu.ufp.sd.inf.rmi.p04_diglib.server.DigLibClient <rmi_registry_ip> <rmi_registry_port> <service_name>");
            System.exit(-1);
        } else {
            try {
                //1. ============ Get info from args ============
                String registryIP = args[0];
                int registryPort = Integer.parseInt(args[1]);
                String serviceName = args[2];

                //2. ============ Get proxy to RMIRegistry ============
                logger.log(Level.INFO, "main(): goint to get reference to RMIRegistry on registryPort = " + registryPort);
                Registry registry = LocateRegistry.getRegistry(registryPort);
                //List available services...
                String[] servicesList = registry.list();
                for (String srvName : servicesList) {
                    logger.log(Level.INFO, "main(): available srvName = " + srvName);
                }

                //3. ============ Use RMIRegistry to get proxy to service ============
                String serviceUrl = "rmi://" + registryIP + ":" + registryPort + "/" + serviceName;
                logger.log(Level.INFO, "main(): goint to get reference to DigLibFactory on serviceUrl = " + serviceUrl);
                DigLibFactoryRI diglibFactoryRI = (DigLibFactoryRI) registry.lookup(serviceUrl);

                //4. ============ Use DigLibFactory to create a DigLibSession and use it ============
                if (diglibFactoryRI != null) {
                    logger.log(Level.INFO, "main(): goint to register user...");
                    User newUser = new User("guest2", "ufp2");
                    if (diglibFactoryRI.register(newUser.getUname(), newUser.getPword())) {
                        logger.log(Level.INFO, "main(): going to login and get DigLibSession...");
                        DigLibSessionRI diglibSessiorRI = diglibFactoryRI.login(newUser.getUname(), newUser.getPword());
                        if (diglibSessiorRI != null) {
                            Book[] books = diglibSessiorRI.search("Distributed", "Tanenbaum");
                            if (books != null) {
                                System.out.println("Found books.length = " + books.length);
                                for (Book b : books) {
                                    System.out.println("Found book: " + b.toString());
                                }
                            } else {
                                logger.log(Level.INFO, "main(): books is null!!");
                            }
                        } else {
                            logger.log(Level.INFO, "main(): diglibSessiorRI is null!!");
                        }
                    }
                } else {
                    logger.log(Level.INFO, "main(): diglibFactoryRI is null!!");
                }
                logger.log(Level.INFO, "main(): Going to finish, bye. ;)");
            } catch (RemoteException | NotBoundException e) {
                //System.out.println(e);
                e.printStackTrace();
            }
        }
    }
}
