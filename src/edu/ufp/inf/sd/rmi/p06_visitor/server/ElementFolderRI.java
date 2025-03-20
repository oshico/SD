/**
 * <p>Title: Projecto SD</p>
 *
 * <p>Description: Projecto apoio aulas SD</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: UFP & INESC Porto</p>
 *
 * @author Rui Moreira
 * @version 1.0
 */

package edu.ufp.inf.sd.rmi.p06_visitor.server;

import java.rmi.*;

public interface ElementFolderRI extends Remote {
    public Object acceptVisitor(VisitorFoldersOperationsI visitor) throws RemoteException;
}

