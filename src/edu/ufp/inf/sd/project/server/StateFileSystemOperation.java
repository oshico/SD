package edu.ufp.inf.sd.project.server;

import java.io.*;
import java.util.ArrayList;

public class StateFileSystemOperation implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String username;
    private final OperationType type;
    private final ArrayList<String> params;


    public enum OperationType {
        CREATEFILE,
        UPDATEFILE,
        DELETEFILE,
        CREATEFOLDER,
        DELETEFOLDER,
    }

    public StateFileSystemOperation(String username, ArrayList<String> params, OperationType type) {
        this.username = username;
        this.params = params;
        this.type = type;
    }

    public static void executeOperation(StateFileSystemOperation sfso) {
        switch (sfso.type) {
            case CREATEFILE:
                String filePath = "/home/oshico/Projects/SD/data/server/" + sfso.params.get(0);
                String fileName = sfso.params.get(1);
                File dir = new File(filePath);
                if (!dir.exists()) {
                    dir.mkdirs();
                    File file = new File(dir, fileName);
                    try {
                        if (file.createNewFile()) {
                            System.out.println("File " + fileName + " created");
                        } else {
                            System.out.println("File " + fileName + " already exists");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    File file = new File(dir, fileName);
                    try {
                        if (file.createNewFile()) {
                            System.out.println("File " + fileName + " created");
                        } else {
                            System.out.println("File " + fileName + " already exists");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case UPDATEFILE:
                String filePath2 = "/home/oshico/Projects/SD/data/server/" + sfso.params.get(0);
                String fileName2 = sfso.params.get(1);
                String fileContent2 = sfso.params.get(2);
                File dir2 = new File(filePath2);
                if (!dir2.exists()) {
                    System.out.println("File " + filePath2 + " is not at " + dir2.getAbsolutePath());
                } else {
                    File file2 = new File(dir2, fileName2);
                    if (!file2.exists()) {
                        System.out.println("File " + filePath2 + " does not exist at " + file2.getAbsolutePath());
                    } else {
                        try {
                            FileWriter fw = new FileWriter(file2);
                            fw.write(fileContent2);
                            fw.flush();
                            System.out.println("File " + fileName2 + " updated");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

                break;
            case DELETEFILE:
                String filePath3 = "/home/oshico/Projects/SD/data/server/" + sfso.params.get(0);
                String fileName3 = sfso.params.get(1);
                File dir3 = new File(filePath3);
                if (!dir3.exists()) {
                    System.out.println("File is not at:" + filePath3);
                } else {
                    File file3 = new File(dir3, fileName3);
                    if (file3.delete()) {
                        System.out.println("File " + fileName3 + " deleted");
                    } else {
                        System.out.println("File " + fileName3 + " does not exist");
                    }
                }
                break;
            case CREATEFOLDER:
                String dirPath4 = "/home/oshico/Projects/SD/data/server/" + sfso.params.get(0);
                File dir4 = new File(dirPath4);
                if (!dir4.exists()) {
                    dir4.mkdirs();
                    System.out.println("Folder created: " + dir4.getAbsolutePath());
                } else {
                    System.out.println("Folder already exists: " + dir4.getAbsolutePath());
                }
                break;
            case DELETEFOLDER:
                String dirPath5 = "/home/oshico/Projects/SD/data/server/" + sfso.params.get(0);
                File dir5 = new File(dirPath5);
                if (dir5.delete()) {
                    System.out.println("Folder deleted: " + dirPath5);
                } else {
                    System.out.println("Folder does not exist: " + dirPath5);
                }
                break;

        }
    }

    public String getUsername() {
        return username;
    }

    public OperationType getType() {
        return type;
    }

    public ArrayList<String> getParams() {
        return params;
    }
}
