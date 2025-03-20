package edu.ufp.inf.sd.rmi.p06_visitor.server;

public class VisitorFoldersOperationCreateFile implements VisitorFoldersOperationsI {

    private String fileToCreate;
    private byte fileContent[];
    private String fileToCreateWithPrefix;

    public VisitorFoldersOperationCreateFile(String fileToCreate) {
        this.fileToCreate = fileToCreate;
    }

    @Override
    public Object visitConcreteElementStateBooks(ElementFolderRI element) {
        SingletonFolderOperationsBooks s = ((ConcreteElementFolderBooksImpl)element).getSingletonStateBooksFolder();
        fileToCreateWithPrefix = "VisitorBook_"+fileToCreate;
        System.out.println("VisitorStateFolderOperationCreateFile - visitConcreteElementStateBooks(): going to create file "+fileToCreateWithPrefix);
        //Specific operation over Books folder
        return s.createFile(fileToCreateWithPrefix);
    }

    @Override
    public Object visitConcreteElementStateMagazines(ElementFolderRI element) {
        SingletonFolderOperationsMagazines s = ((ConcreteElementFolderMagazinesImpl)element).getSingletonStateMagazinesFolder();
        fileToCreateWithPrefix = "VisitorMagazine_"+fileToCreate;
        System.out.println("VisitorStateFolderOperationCreateFile - visitConcreteElementStateMagazines(): going to create file "+fileToCreateWithPrefix);
        //Specific operation over Magazines folder
        return s.createFile(fileToCreateWithPrefix);
    }

    /**
     * @return the fileToCreate
     */
    public String getFileToCreate() {
        return fileToCreate;
    }

    /**
     * @param fileToCreate the fileToCreate to set
     */
    public void setFileToCreate(String fileToCreate) {
        this.fileToCreate = fileToCreate;
    }
}
