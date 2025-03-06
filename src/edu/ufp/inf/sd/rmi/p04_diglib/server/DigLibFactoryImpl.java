package edu.ufp.inf.sd.rmi.p04_diglib.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DigLibFactoryImpl extends UnicastRemoteObject implements DigLibFactoryRI {

    private final DBMock db;

    Logger logger = Logger.getLogger(DigLibFactoryImpl.class.getName());

    public DigLibFactoryImpl() throws RemoteException {
        super();
        this.db = new DBMock();
    }

    public DigLibSessionRI login(String uname, String pw) throws RemoteException {
        logger.log(Level.INFO, "login(${uname}, ${pw})".replace("${uname}", uname).replace("${pw}", pw));
        if (db.exists(uname, pw)) {
            DigLibSessionRI digLibSessionRI = new DigLibSessionImpl(this.db, new User(uname, pw));
            db.putDigLibSession(uname, digLibSessionRI);
            return digLibSessionRI;
        }
        logger.log(Level.INFO, "login(${uname}, ${pw}) user does not exist!".replace("${uname}", uname).replace("${pw}", pw));
        return null;
    }

    public boolean register(String uname, String pw) throws RemoteException {
        logger.log(Level.INFO, "register(${uname}, ${pw})".replace("${uname}", uname).replace("${pw}", pw));
        if (!db.exists(uname, pw)) {
            db.register(uname, pw);
            return true;
        }
        return false;
    }
}
