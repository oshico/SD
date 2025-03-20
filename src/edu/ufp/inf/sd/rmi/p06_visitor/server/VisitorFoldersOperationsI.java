package edu.ufp.inf.sd.rmi.p06_visitor.server;

import java.io.Serializable;

public interface VisitorFoldersOperationsI extends Serializable {
    public Object visitConcreteElementStateBooks(ElementFolderRI element);
    public Object visitConcreteElementStateMagazines(ElementFolderRI element);
}
