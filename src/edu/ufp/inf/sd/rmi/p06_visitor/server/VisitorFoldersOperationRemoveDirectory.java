package edu.ufp.inf.sd.rmi.p06_visitor.server;

public class VisitorFoldersOperationRemoveDirectory implements VisitorFoldersOperationsI {
    private String directoryToDelete;
    private String directoryToDeleteWithPrefix;

    public VisitorFoldersOperationRemoveDirectory(String newFolder) {
        this.directoryToDelete = newFolder;
    }

    @Override
    public Object visitConcreteElementStateBooks(ElementFolderRI element) {
        SingletonFolderOperationsBooks s = ((ConcreteElementFolderBooksImpl)element).getSingletonStateBooksFolder();
        directoryToDeleteWithPrefix = "VisitorBook_"+ directoryToDelete;
        System.out.println("VisitorFoldersOperationDeleteFile - visitConcreteElementStateBooks(): going to delete file "+ directoryToDeleteWithPrefix);
        //Specific operation over Books folder
        return s.deleteFile(directoryToDeleteWithPrefix);
    }

    @Override
    public Object visitConcreteElementStateMagazines(ElementFolderRI element) {
        SingletonFolderOperationsMagazines s = ((ConcreteElementFolderMagazinesImpl)element).getSingletonStateMagazinesFolder();
        directoryToDeleteWithPrefix = "VisitorMagazine_"+ directoryToDelete;
        System.out.println("VisitorFoldersOperationDeleteFile - visitConcreteElementStateMagazines(): going to delete file "+ directoryToDeleteWithPrefix);
        //Specific operation over Magazines folder
        return s.deleteFile(directoryToDeleteWithPrefix);
    }


    /**
     * @return the fileToDelete
     */
    public String getDirectoryToDelete() {
        return directoryToDelete;
    }

    /**
     * @param directoryToDelete the fileToDelete to set
     */
    public void setDirectoryToDelete(String directoryToDelete) {
        this.directoryToDelete = directoryToDelete;
    }
}
