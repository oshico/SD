package edu.ufp.inf.sd.rmi.p06_visitor.client;

import edu.ufp.inf.sd.rmi.p06_visitor.server.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class VisitorElementsFoldersClient {

    static ElementFolderRI booksRI;

    public static void main(String[] args) {
        try {
            String serviceNames[]={
                    ElementsFoldersServer.SERVICE_NAME_ELEMENT_STATE_BOOKS,
                    ElementsFoldersServer.SERVICE_NAME_ELEMENT_STATE_MAGAZINES
            };

            //Get registry
            Registry registry=LocateRegistry.getRegistry(ElementsFoldersServer.REGISTRY_PORT);

            // 1st Create ObjectStructureElementsFolders to lookup remote visitor services
            System.out.println("VisitorElementsFoldersClient - main(): create ObjectStructureElementsFolders for service = " + serviceNames[0]);
            ObjectStructureElementsFolders elements=new ObjectStructureElementsFolders(serviceNames);

            // 2nd Create VisitorFoldersOperationCreateFile to be sent to remote services
            String newfile="NewFile.txt";
            System.out.println("VisitorElementsFoldersClient - main(): create VisitorFoldersOperationCreateFile for newfile = " + newfile);
            VisitorFoldersOperationCreateFile visitorFoldersOperationCreateFile=new VisitorFoldersOperationCreateFile(newfile);
            VisitorFoldersOperationCreateDirectory visitorFoldersOperationCreateDirectory=new VisitorFoldersOperationCreateDirectory(newfile);

            // 3rd Use ObjectStructureElementsFolders to dispatch a visitor operation to remote services
            System.out.println("VisitorElementsFoldersClient - main(): dispatch VisitorFoldersOperationCreateFile for newfile = " + newfile);
            Boolean b=elements.dispatchVisitorFoldersOperation(visitorFoldersOperationCreateFile);
            Boolean c=elements.dispatchVisitorFoldersOperation(visitorFoldersOperationCreateDirectory);
            System.out.println("VisitorElementsFoldersClient - main(): dispatched visitorFoldersOperationCreateFile = " + newfile + " with result = " + b);
            System.out.println("VisitorElementsFoldersClient - main(): dispatched visitorFoldersOperationCreateFile = " + newfile + " with result = " + c);


        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
