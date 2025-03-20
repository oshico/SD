package edu.ufp.inf.sd.rmi.p06_visitor.client;

import edu.ufp.inf.sd.rmi.p06_visitor.server.ElementFolderRI;
import edu.ufp.inf.sd.rmi.p06_visitor.server.VisitorFoldersOperationsI;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rui
 */
public class ObjectStructureElementsFolders {

    public ArrayList<ElementFolderRI> elements = new ArrayList();

    public ObjectStructureElementsFolders(String serviceNames[]) {
        //Lookup stubs/proxies for existing visitor remote services
        for (String serviceName : serviceNames) {
            try {
                ElementFolderRI eRI = (ElementFolderRI) Naming.lookup(serviceName);
                this.addElementFolderRI(eRI);
            } catch (NotBoundException | MalformedURLException | RemoteException ex) {
                Logger.getLogger(ObjectStructureElementsFolders.class.getName()).log(Level.WARNING, null, ex);
            }
        }
    }

    public final void addElementFolderRI(ElementFolderRI e) {
        this.elements.add(e);
    }

    public Boolean dispatchVisitorFoldersOperation(VisitorFoldersOperationsI visitor) {
        for (ElementFolderRI element : elements) {
            try {
                element.acceptVisitor(visitor);
            } catch (RemoteException ex) {
                Logger.getLogger(ObjectStructureElementsFolders.class.getName()).log(Level.WARNING, null, ex);
                return false;
            }
        }
        return true;
    }
}
