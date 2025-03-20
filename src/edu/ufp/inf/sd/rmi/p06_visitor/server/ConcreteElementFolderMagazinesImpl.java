package edu.ufp.inf.sd.rmi.p06_visitor.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ConcreteElementFolderMagazinesImpl extends UnicastRemoteObject implements ElementFolderRI {

    private final SingletonFolderOperationsMagazines singletonStateMagazinesFolder;

    public ConcreteElementFolderMagazinesImpl(String booksFolder) throws RemoteException {
        super();
        //this.stateBooksFolder = new SingletonFolderOperationsBooks(booksFolder);
        this.singletonStateMagazinesFolder = SingletonFolderOperationsMagazines.createSingletonFolderOperationsBooks(booksFolder);
    }

    @Override
    public Object acceptVisitor(VisitorFoldersOperationsI visitor) throws RemoteException {
        Object o = visitor.visitConcreteElementStateBooks(this);
        return o;
    }

    public SingletonFolderOperationsMagazines getSingletonStateMagazinesFolder() {
        return singletonStateMagazinesFolder;
    }
}

