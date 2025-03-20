package edu.ufp.inf.sd.rmi.p06_visitor.server;

public class VisitorFoldersOperationCreateDirectory implements VisitorFoldersOperationsI {

    private String directoryToCreate;
    private String directoryToCreateWithPrefix;

    public VisitorFoldersOperationCreateDirectory(String fileToCreate) {
        this.directoryToCreate = fileToCreate;
    }

    @Override
    public Object visitConcreteElementStateBooks(ElementFolderRI element) {
        SingletonFolderOperationsBooks s = ((ConcreteElementFolderBooksImpl)element).getSingletonStateBooksFolder();
        directoryToCreateWithPrefix = "VisitorBook_"+ directoryToCreate;
        System.out.println("VisitorStateFolderOperationCreateDirectory - visitConcreteElementStateBooks(): going to create file "+ directoryToCreateWithPrefix);
        //Specific operation over Books folder
        return s.createDirectory(directoryToCreateWithPrefix);
    }

    @Override
    public Object visitConcreteElementStateMagazines(ElementFolderRI element) {
        SingletonFolderOperationsMagazines s = ((ConcreteElementFolderMagazinesImpl)element).getSingletonStateMagazinesFolder();
        directoryToCreateWithPrefix = "VisitorMagazine_"+ directoryToCreate;
        System.out.println("VisitorStateFolderOperationCreateDirectory - visitConcreteElementStateMagazines(): going to create file "+ directoryToCreateWithPrefix);
        //Specific operation over Magazines folder
        return s.createDirectory(directoryToCreateWithPrefix);
    }

    /**
     * @return the fileToCreate
     */
    public String getDirectoryToCreate() {
        return directoryToCreate;
    }

    /**
     * @param fileToCreate the fileToCreate to set
     */
    public void setDirectoryToCreate(String fileToCreate) {
        this.directoryToCreate = directoryToCreate;
    }
}
