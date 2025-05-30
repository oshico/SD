package edu.ufp.inf.sd.rmi.p06_visitor.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ConcreteElementFolderBooksImpl extends UnicastRemoteObject implements ElementFolderRI {

    private final SingletonFolderOperationsBooks singletonStateBooksFolder;

    public ConcreteElementFolderBooksImpl(String booksFolder) throws RemoteException {
        super();
        //this.stateBooksFolder = new SingletonFolderOperationsBooks(booksFolder);
        this.singletonStateBooksFolder= SingletonFolderOperationsBooks.createSingletonFolderOperationsBooks(booksFolder);
    }

    @Override
    public Object acceptVisitor(VisitorFoldersOperationsI visitor) throws RemoteException {
        Object o = visitor.visitConcreteElementStateBooks(this);
        return o;
    }

    public SingletonFolderOperationsBooks getSingletonStateBooksFolder() {
        return singletonStateBooksFolder;
    }

}

