package edu.ufp.inf.sd.rmi.p06_visitor.server;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SingletonFolderOperationsMagazines implements SingletonFoldersOperationsI {
    private static SingletonFolderOperationsMagazines singletonFolderOperationsMagazines;
    private final File rootFolderMagazines;

    /** private - Avoid direct instantiation */
    private SingletonFolderOperationsMagazines(String folder) {
        rootFolderMagazines = new File(folder);
    }

    public synchronized static SingletonFolderOperationsMagazines createSingletonFolderOperationsBooks(String folder){
        if (singletonFolderOperationsMagazines ==null){
            singletonFolderOperationsMagazines = new SingletonFolderOperationsMagazines(folder);
        }
        return singletonFolderOperationsMagazines;
    }

    @Override
    public Boolean createFile(String fname) {
        return false;
    }

    @Override
    public Boolean deleteFile(String fname) {
        return false;
    }

    public Boolean createDirectory(String dname) {
        try {
            File newFileDir = new File(this.rootFolderMagazines.getAbsolutePath() + "/" + dname);
            return newFileDir.mkdir();
        } catch (Exception ex) {
            Logger.getLogger(SingletonFolderOperationsBooks.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    public Boolean removeDirectory(String dname) {
        try {
            File newFileDir = new File(this.rootFolderMagazines.getAbsolutePath() + "/" + dname);
            return newFileDir.delete();
        } catch (Exception ex) {
            Logger.getLogger(SingletonFolderOperationsBooks.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
