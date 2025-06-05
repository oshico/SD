package edu.ufp.inf.sd.project.client;

import edu.ufp.inf.sd.project.server.*;
import edu.ufp.inf.sd.project.util.SetupContextRMI;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class ClientMain {
    private Registry registry;
    private AuthServiceRI authService;
    private SessionRI currentSession;
    private Scanner scanner;
    private ObserverImpl observer;
    private boolean isLoggedIn = false;

    public ClientMain() {
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: java ClientMain <registry_host> <registry_port> <service_name>");
            System.exit(1);
        }

        String registryHost = args[0];
        String registryPort = args[1];
        String serviceName = args[2];

        ClientMain client = new ClientMain();
        client.lookupService(registryHost, registryPort, serviceName);
        client.runTerminalInterface();
    }

    private void lookupService(String registryHost, String registryPort, String serviceName) {
        try {
            // Get the registry
            SetupContextRMI contextRMI = new SetupContextRMI(
                    this.getClass(), registryHost, registryPort, new String[]{serviceName});

            // Get the service URL
            String serviceUrl = contextRMI.getServicesUrl(0);
            System.out.println("Service URL: " + serviceUrl);

            // Get the registry
            registry = contextRMI.getRegistry();

            // Lookup service on registry
            authService = (AuthServiceRI) registry.lookup(serviceName);

            System.out.println("✓ Connected to service '" + serviceName + "' at " + registryHost + ":" + registryPort);
            System.out.println("═════════════════════════════════════════════════════════════");

        } catch (RemoteException | NotBoundException e) {
            System.err.println("✗ Error connecting to server: " + e.getMessage());
            System.exit(1);
        }
    }

    private void runTerminalInterface() {
        printWelcomeBanner();

        while (true) {
            try {
                if (!isLoggedIn) {
                    showAuthMenu();
                } else {
                    showMainMenu();
                }
            } catch (Exception e) {
                System.err.println("✗ Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void printWelcomeBanner() {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║              COLLABORATIVE EDITOR CLIENT                  ║");
        System.out.println("║                   Terminal Interface                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private void showAuthMenu() throws RemoteException {
        System.out.println("\n┌─ AUTHENTICATION MENU ─────────────────────────────────────┐");
        System.out.println("│ 1. Login                                                  │");
        System.out.println("│ 2. Register                                               │");
        System.out.println("│ 3. Exit                                                   │");
        System.out.println("└───────────────────────────────────────────────────────────┘");
        System.out.print("Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                handleLogin();
                break;
            case 2:
                handleRegister();
                break;
            case 3:
                handleExit();
                break;
            default:
                System.out.println("✗ Invalid option. Please try again.");
        }
    }

    private void showMainMenu() throws RemoteException {
        System.out.println("\n┌─ MAIN MENU ───────────────────────────────────────────────┐");
        System.out.println("│ 1. File Operations                                        │");
        System.out.println("│ 2. Folder Operations                                      │");
        System.out.println("│ 3. Sharing Operations                                     │");
        System.out.println("│ 4. View Shared Files                                     │");
        System.out.println("│ 5. Observer Operations                                    │");
        System.out.println("│ 6. User Info                                              │");
        System.out.println("│ 7. Logout                                                 │");
        System.out.println("└───────────────────────────────────────────────────────────┘");
        System.out.print("Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                showFileOperationsMenu();
                break;
            case 2:
                showFolderOperationsMenu();
                break;
            case 3:
                showSharingMenu();
                break;
            case 4:
                viewSharedFiles();
                break;
            case 5:
                showObserverMenu();
                break;
            case 6:
                showUserInfo();
                break;
            case 7:
                handleLogout();
                break;
            default:
                System.out.println("✗ Invalid option. Please try again.");
        }
    }

    private void handleLogin() throws RemoteException {
        System.out.println("\n═══ LOGIN ═══");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("✗ Username and password cannot be empty.");
            return;
        }

        SessionRI session = authService.login(username, password);
        if (session != null) {
            currentSession = session;
            isLoggedIn = true;
            System.out.println("✓ Login successful! Welcome, " + username + "!");

            // Initialize observer
            try {
                observer = new ObserverImpl(this);
                System.out.println("✓ Observer initialized successfully.");
            } catch (RemoteException e) {
                System.out.println("⚠ Warning: Could not initialize observer: " + e.getMessage());
            }
        } else {
            System.out.println("✗ Invalid username or password.");
        }
    }

    private void handleRegister() throws RemoteException {
        System.out.println("\n═══ REGISTER ═══");
        System.out.print("Username (alphanumeric only): ");
        String username = scanner.nextLine().trim();
        System.out.print("Password (minimum 4 characters): ");
        String password = scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("✗ Username and password cannot be empty.");
            return;
        }

        boolean success = authService.register(username, password);
        if (success) {
            System.out.println("✓ Registration successful! You can now login.");
        } else {
            System.out.println("✗ Registration failed. Username may already exist or doesn't meet requirements.");
        }
    }

    private void showFileOperationsMenu() throws RemoteException {
        System.out.println("\n┌─ FILE OPERATIONS ─────────────────────────────────────────┐");
        System.out.println("│ 1. Create File                                            │");
        System.out.println("│ 2. Update File                                            │");
        System.out.println("│ 3. Delete File                                            │");
        System.out.println("│ 4. Back to Main Menu                                     │");
        System.out.println("└───────────────────────────────────────────────────────────┘");
        System.out.print("Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                createFile();
                break;
            case 2:
                updateFile();
                break;
            case 3:
                deleteFile();
                break;
            case 4:
                return;
            default:
                System.out.println("✗ Invalid option. Please try again.");
        }
    }

    private void showFolderOperationsMenu() throws RemoteException {
        System.out.println("\n┌─ FOLDER OPERATIONS ───────────────────────────────────────┐");
        System.out.println("│ 1. Create Folder                                          │");
        System.out.println("│ 2. Delete Folder                                          │");
        System.out.println("│ 3. Back to Main Menu                                     │");
        System.out.println("└───────────────────────────────────────────────────────────┘");
        System.out.print("Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                createFolder();
                break;
            case 2:
                deleteFolder();
                break;
            case 3:
                return;
            default:
                System.out.println("✗ Invalid option. Please try again.");
        }
    }

    private void createFile() throws RemoteException {
        System.out.println("\n═══ CREATE FILE ═══");
        System.out.print("Enter file path (e.g., test): ");
        String filePath = currentSession.getUsername() + "/" + scanner.nextLine().trim();
        System.out.print("Enter file name (e.g., document.txt): ");
        String fileName = scanner.nextLine().trim();

        if (filePath.isEmpty() || fileName.isEmpty()) {
            System.out.println("✗ File path and name cannot be empty.");
            return;
        }

        String filePathLocal = "/home/oshico/Projects/SD/data/" + filePath;
        File dir = new File(filePathLocal);
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

        ArrayList<String> params = new ArrayList<>();
        params.add(filePath);
        params.add(fileName);

        StateFileSystemOperation operation = new StateFileSystemOperation(
                currentSession.getUsername(), params, StateFileSystemOperation.OperationType.CREATEFILE
        );

        SubjectFileSystemRI fileSystem = currentSession.getFileSystem();
        fileSystem.setStateFileSystemOperation(operation);
        fileSystem.notifyObservers(operation);

        System.out.println("✓ File creation operation sent successfully!");
    }

    private void updateFile() throws RemoteException {
        System.out.println("\n═══ UPDATE FILE ═══");
        System.out.print("Enter file path: ");
        String filePath = currentSession.getUsername() + "/" + scanner.nextLine().trim();
        System.out.print("Enter file name: ");
        String fileName = scanner.nextLine().trim();
        System.out.println("Enter file content (press Enter twice to finish):");

        StringBuilder content = new StringBuilder();
        String line;
        int emptyLines = 0;

        while (emptyLines < 2) {
            line = scanner.nextLine();
            if (line.isEmpty()) {
                emptyLines++;
            } else {
                emptyLines = 0;
                content.append(line).append("\n");
            }
        }

        if (filePath.isEmpty() || fileName.isEmpty()) {
            System.out.println("✗ File path and name cannot be empty.");
            return;
        }

        String filePathLocal = "/home/oshico/Projects/SD/data/" + filePath;
        File dir = new File(filePathLocal);
        if (!dir.exists()) {
            System.out.println("File " + filePathLocal + " is not at " + dir.getAbsolutePath());
        } else {
            File file = new File(dir, fileName);
            if (!file.exists()) {
                System.out.println("File " + filePathLocal + " does not exist at " + file.getAbsolutePath());
            } else {
                try {
                    FileWriter fw = new FileWriter(file);
                    fw.write(content.toString());
                    fw.flush();
                    System.out.println("File " + fileName + " updated");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        ArrayList<String> params = new ArrayList<>();
        params.add(filePath);
        params.add(fileName);
        params.add(content.toString());

        StateFileSystemOperation operation = new StateFileSystemOperation(
                currentSession.getUsername(), params, StateFileSystemOperation.OperationType.UPDATEFILE
        );

        SubjectFileSystemRI fileSystem = currentSession.getFileSystem();
        fileSystem.setStateFileSystemOperation(operation);
        fileSystem.notifyObservers(operation);

        System.out.println("✓ File update operation sent successfully!");
    }

    private void deleteFile() throws RemoteException {
        System.out.println("\n═══ DELETE FILE ═══");
        System.out.print("Enter file path: ");
        String filePath = currentSession.getUsername() + "/" + scanner.nextLine().trim();
        System.out.print("Enter file name: ");
        String fileName = scanner.nextLine().trim();

        if (filePath.isEmpty() || fileName.isEmpty()) {
            System.out.println("✗ File path and name cannot be empty.");
            return;
        }

        String filePathLocal = "/home/oshico/Projects/SD/data/" + filePath;
        File dir = new File(filePathLocal);
        if (!dir.exists()) {
            System.out.println("File is not at:" + filePathLocal);
        } else {
            File file3 = new File(dir, fileName);
            if (file3.delete()) {
                System.out.println("File " + fileName + " deleted");
            } else {
                System.out.println("File " + fileName + " does not exist");
            }
        }

        ArrayList<String> params = new ArrayList<>();
        params.add(filePath);
        params.add(fileName);

        StateFileSystemOperation operation = new StateFileSystemOperation(
                currentSession.getUsername(), params, StateFileSystemOperation.OperationType.DELETEFILE
        );

        SubjectFileSystemRI fileSystem = currentSession.getFileSystem();
        fileSystem.setStateFileSystemOperation(operation);
        fileSystem.notifyObservers(operation);

        System.out.println("✓ File deletion operation sent successfully!");
    }

    private void createFolder() throws RemoteException {
        System.out.println("\n═══ CREATE FOLDER ═══");
        System.out.print("Enter folder path (e.g., folder folder/folder): ");
        String folderPath = currentSession.getUsername() + "/" + scanner.nextLine().trim();

        if (folderPath.isEmpty()) {
            System.out.println("✗ Folder path cannot be empty.");
            return;
        }

        String folderPathLocal = "/home/oshico/Projects/SD/data/" + folderPath;
        File dir = new File(folderPathLocal);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("Folder created: " + dir.getAbsolutePath());
        } else {
            System.out.println("Folder already exists: " + dir.getAbsolutePath());
        }

        ArrayList<String> params = new ArrayList<>();
        params.add(folderPath);

        StateFileSystemOperation operation = new StateFileSystemOperation(
                currentSession.getUsername(), params, StateFileSystemOperation.OperationType.CREATEFOLDER
        );

        SubjectFileSystemRI fileSystem = currentSession.getFileSystem();
        fileSystem.setStateFileSystemOperation(operation);
        fileSystem.notifyObservers(operation);

        System.out.println("✓ Folder creation operation sent successfully!");
    }

    private void deleteFolder() throws RemoteException {
        System.out.println("\n═══ DELETE FOLDER ═══");
        System.out.print("Enter folder path: ");
        String folderPath = currentSession.getUsername() + "/" + scanner.nextLine().trim();

        if (folderPath.isEmpty()) {
            System.out.println("✗ Folder path cannot be empty.");
            return;
        }

        String folderPathLocal = "/home/oshico/Projects/SD/data/" + folderPath;
        File dir = new File(folderPathLocal);
        if (dir.delete()) {
            System.out.println("Folder deleted: " + folderPathLocal);
        } else {
            System.out.println("Folder does not exist: " + folderPathLocal);
        }


        ArrayList<String> params = new ArrayList<>();
        params.add(folderPath);

        StateFileSystemOperation operation = new StateFileSystemOperation(
                currentSession.getUsername(), params, StateFileSystemOperation.OperationType.DELETEFOLDER
        );

        SubjectFileSystemRI fileSystem = currentSession.getFileSystem();
        fileSystem.setStateFileSystemOperation(operation);
        fileSystem.notifyObservers(operation);

        System.out.println("✓ Folder deletion operation sent successfully!");
    }

    private void showSharingMenu() throws RemoteException {
        System.out.println("\n┌─ SHARING OPERATIONS ──────────────────────────────────────┐");
        System.out.println("│ 1. Share filesystem with user                            │");
        System.out.println("│ 2. Unshare filesystem with user                          │");
        System.out.println("│ 3. Back to Main Menu                                     │");
        System.out.println("└───────────────────────────────────────────────────────────┘");
        System.out.print("Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                shareFileSystem();
                break;
            case 2:
                unshareFileSystem();
                break;
            case 3:
                return;
            default:
                System.out.println("✗ Invalid option. Please try again.");
        }
    }

    private void shareFileSystem() throws RemoteException {
        System.out.println("\n═══ SHARE FILESYSTEM ═══");
        System.out.print("Enter username to share with: ");
        String targetUser = scanner.nextLine().trim();

        if (targetUser.isEmpty()) {
            System.out.println("✗ Username cannot be empty.");
            return;
        }

        if (targetUser.equals(currentSession.getUsername())) {
            System.out.println("✗ You cannot share with yourself.");
            return;
        }

        currentSession.shareWithFileSystem(targetUser);
        System.out.println("✓ Filesystem shared with user: " + targetUser);
    }

    private void unshareFileSystem() throws RemoteException {
        System.out.println("\n═══ UNSHARE FILESYSTEM ═══");
        System.out.print("Enter username to unshare with: ");
        String targetUser = scanner.nextLine().trim();

        if (targetUser.isEmpty()) {
            System.out.println("✗ Username cannot be empty.");
            return;
        }

        currentSession.unshareWithFileSystem(targetUser);
        System.out.println("✓ Filesystem unshared with user: " + targetUser);
    }

    private void viewSharedFiles() throws RemoteException {
        System.out.println("\n═══ SHARED WITH ME ═══");
        Map<String, SubjectFileSystemRI> sharedFileSystems = currentSession.getSharedWithMeFileSystem();

        if (sharedFileSystems == null || sharedFileSystems.isEmpty()) {
            System.out.println("No files are currently shared with you.");
            return;
        }

        System.out.println("Files shared with you:");
        System.out.println("┌─────────────────────────────────────────────────────────┐");
        for (String owner : sharedFileSystems.keySet()) {
            System.out.println("│ Owner: " + owner + "                                   │");
        }
        System.out.println("└─────────────────────────────────────────────────────────┘");
    }

    private void showObserverMenu() throws RemoteException {
        System.out.println("\n┌─ OBSERVER OPERATIONS ─────────────────────────────────────┐");
        System.out.println("│ 1. Attach Observer to My FileSystem                      │");
        System.out.println("│ 2. Detach Observer from My FileSystem                    │");
        System.out.println("│ 3. Back to Main Menu                                     │");
        System.out.println("└───────────────────────────────────────────────────────────┘");
        System.out.print("Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                attachObserver();
                break;
            case 2:
                detachObserver();
                break;
            case 3:
                return;
            default:
                System.out.println("✗ Invalid option. Please try again.");
        }
    }

    private void attachObserver() throws RemoteException {
        if (observer == null) {
            System.out.println("✗ Observer not initialized. Please restart the client.");
            return;
        }

        SubjectFileSystemRI fileSystem = currentSession.getFileSystem();
        fileSystem.attachObserver(observer);
        System.out.println("✓ Observer attached to your filesystem. You will now receive notifications of changes.");
    }

    private void detachObserver() throws RemoteException {
        if (observer == null) {
            System.out.println("✗ Observer not initialized.");
            return;
        }

        SubjectFileSystemRI fileSystem = currentSession.getFileSystem();
        fileSystem.detachObserver(observer);
        System.out.println("✓ Observer detached from your filesystem.");
    }

    private void showUserInfo() throws RemoteException {
        System.out.println("\n═══ USER INFORMATION ═══");
        System.out.println("┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ Username: " + currentSession.getUsername() + "                                 │");
        System.out.println("│ Status: Logged In                                      │");
        System.out.println("│ Observer: " + (observer != null ? "Initialized" : "Not Initialized") + "                              │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
    }

    private void handleLogout() throws RemoteException {
        System.out.println("\n═══ LOGOUT ═══");
        System.out.print("Are you sure you want to logout? (y/N): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("y") || confirmation.equals("yes")) {
            if (currentSession != null) {
                currentSession.logout();
                currentSession = null;
            }
            isLoggedIn = false;
            observer = null;
            System.out.println("✓ Logged out successfully!");
        } else {
            System.out.println("Logout cancelled.");
        }
    }

    private void handleExit() {
        System.out.println("\n═══ EXIT ═══");
        System.out.print("Are you sure you want to exit? (y/N): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("y") || confirmation.equals("yes")) {
            try {
                if (isLoggedIn && currentSession != null) {
                    currentSession.logout();
                }
            } catch (RemoteException e) {
                System.err.println("Error during logout: " + e.getMessage());
            }
            System.out.println("✓ Goodbye!");
            System.exit(0);
        } else {
            System.out.println("Exit cancelled.");
        }
    }

    private int getIntInput() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("✗ Please enter a valid number: ");
            }
        }
    }
}