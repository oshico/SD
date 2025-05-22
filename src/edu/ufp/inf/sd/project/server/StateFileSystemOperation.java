package edu.ufp.inf.sd.project.server;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
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

                break;
            case UPDATEFILE:

                break;
            case DELETEFILE:

        }
   }
}
