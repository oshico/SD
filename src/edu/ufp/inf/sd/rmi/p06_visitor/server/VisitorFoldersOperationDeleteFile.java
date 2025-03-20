package edu.ufp.inf.sd.rmi.p06_visitor.server;

public class VisitorFoldersOperationDeleteFile implements VisitorFoldersOperationsI {

    private String fileToDelete;
    private String fileToDeleteWithPrefix;

    public VisitorFoldersOperationDeleteFile(String newFolder) {
        this.fileToDelete = newFolder;
    }

    @Override
    public Object visitConcreteElementStateBooks(ElementFolderRI element) {
        SingletonFolderOperationsBooks s = ((ConcreteElementFolderBooksImpl)element).getSingletonStateBooksFolder();
        fileToDeleteWithPrefix = "VisitorBook_"+fileToDelete;
        System.out.println("VisitorFoldersOperationDeleteFile - visitConcreteElementStateBooks(): going to delete file "+fileToDeleteWithPrefix);
        //Specific operation over Books folder
        return s.deleteFile(fileToDeleteWithPrefix);
    }

    @Override
    public Object visitConcreteElementStateMagazines(ElementFolderRI element) {
        SingletonFolderOperationsMagazines s = ((ConcreteElementFolderMagazinesImpl)element).getSingletonStateMagazinesFolder();
        fileToDeleteWithPrefix = "VisitorMagazine_"+fileToDelete;
        System.out.println("VisitorFoldersOperationDeleteFile - visitConcreteElementStateMagazines(): going to delete file "+fileToDeleteWithPrefix);
        //Specific operation over Magazines folder
        return s.deleteFile(fileToDeleteWithPrefix);
    }


    /**
     * @return the fileToDelete
     */
    public String getFileToDelete() {
        return fileToDelete;
    }

    /**
     * @param fileToDelete the fileToDelete to set
     */
    public void setFileToDelete(String fileToDelete) {
        this.fileToDelete = fileToDelete;
    }
}
