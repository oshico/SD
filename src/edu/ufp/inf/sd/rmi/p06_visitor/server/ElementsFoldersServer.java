package edu.ufp.inf.sd.rmi.p06_visitor.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ElementsFoldersServer {

    public static final int REGISTRY_PORT=1099;
    public static final String SERVICE_NAME_ELEMENT_STATE_BOOKS = "rmi://localhost:1099/VisitorBooksService";
    public static final String ROOT_FOLDER_NAME_ELEMENT_STATE_BOOKS = "/Users/rui/Documents/NetBeansProjects/SD/rootfoldervisitors/Books";

    public static final String SERVICE_NAME_ELEMENT_STATE_MAGAZINES = "rmi://localhost:1097/VisitorMagazinesService";
    public static final String ROOT_FOLDER_NAME_ELEMENT_STATE_MAGAZINES = "/Users/rui/Documents/NetBeansProjects/SD/rootfoldervisitors/Magazines";

    public static void main(String[] args) {
        try {
            /* No need now!!
            // Create and install a security manager
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }
            */

            //Create registry
            Registry registry = LocateRegistry.createRegistry(REGISTRY_PORT);

            //String hostIP = InetAddress.getLocalHost().getHostAddress();
            System.out.println("ElementsFoldersServer - main(): register " + SERVICE_NAME_ELEMENT_STATE_BOOKS + " ...");
            System.out.println("ElementsFoldersServer - main(): register " + SERVICE_NAME_ELEMENT_STATE_MAGAZINES + " ...");
            
            ElementFolderRI elementBooksRI = new ConcreteElementFolderBooksImpl(ROOT_FOLDER_NAME_ELEMENT_STATE_BOOKS);
            ElementFolderRI elementMagazinesRI = new ConcreteElementFolderMagazinesImpl(ROOT_FOLDER_NAME_ELEMENT_STATE_MAGAZINES);
            
            Naming.rebind(SERVICE_NAME_ELEMENT_STATE_BOOKS, elementBooksRI);
            Naming.rebind(SERVICE_NAME_ELEMENT_STATE_MAGAZINES, elementMagazinesRI);
            
            System.out.println("ElementsFoldersServer - main(): wainting for visitors...");
        } catch (RemoteException | MalformedURLException e) {
            Logger.getLogger(ElementsFoldersServer.class.getName()).log(Level.WARNING, e.toString());
        }
    }
}
