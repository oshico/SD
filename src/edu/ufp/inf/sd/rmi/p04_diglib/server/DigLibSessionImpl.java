package edu.ufp.inf.sd.rmi.p04_diglib.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class DigLibSessionImpl extends UnicastRemoteObject implements DigLibSessionRI {

    final DBMock db;
    final User user;

    public DigLibSessionImpl(DBMock db, User user) throws RemoteException {
        super();
        this.db = db;
        this.user = user;
    }

    public Book[] search(String title, String author) throws RemoteException {
        return this.db.select(title, author);
    }
    public void logout() throws RemoteException {
        this.db.removeDigLibSession(user.getUname(),this);
    }
}
