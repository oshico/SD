package edu.ufp.inf.sd.rmi.p06_visitor.server;

/**
 *
 * @author rui
 */
public interface SingletonFoldersOperationsI {
    public Boolean createFile(String fname);
    public Boolean deleteFile(String fname);
    public Boolean createDirectory(String dname);
    public Boolean removeDirectory(String dname);
}
