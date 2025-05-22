package edu.ufp.inf.sd.project.client;

import edu.ufp.inf.sd.project.server.AuthServiceRI;
import edu.ufp.inf.sd.project.server.SessionRI;
import edu.ufp.inf.sd.project.server.SubjectFileSystemRI;
import edu.ufp.inf.sd.project.util.SetupContextRMI;

import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Scanner;


public class ClientMain {
    private Registry registry;
    private AuthServiceRI authService;
    private SessionRI session;
    private SubjectFileSystemRI fileSystem;
    private ObserverRI observer;

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

            System.out.println("Found service " + serviceName + " at " + registryHost + ":" + registryPort);

            runConsole();

        } catch (RemoteException | NotBoundException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            System.exit(1);
        }
    }

    private void runConsole() {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean loggedIn = false;

            while (!loggedIn) {
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.print("Choose option: ");
                String choice = scanner.nextLine();

                System.out.print("Username: ");
                String username = scanner.nextLine();
                System.out.print("Password: ");
                String password = scanner.nextLine();

                switch (choice) {
                    case "1":
                        if (authService.register(username, password)) {
                            System.out.println("Registered successfully!");
                        } else {
                            System.out.println("Username already exists.");
                        }
                        break;
                    case "2":
                        session = authService.login(username, password);
                        if (session != null) {
                            observer =
                            System.out.println("Logged in successfully.");
                            loggedIn = true;
                        } else {
                            System.out.println("Invalid credentials.");
                        }
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            }

            commandLoop(scanner);

        } catch (RemoteException e) {
            System.err.println("Remote error: " + e.getMessage());
        }
    }

    private void commandLoop(Scanner scanner) throws RemoteException {
        while (true) {
            System.out.println("\nAvailable commands:");
            System.out.println("1. Create Document");
            System.out.println("2. Open Document");
            System.out.println("3. Edit Document");
            System.out.println("4. Switch Folder");
            System.out.println("5. Share Folder");
            System.out.println("6. Exit");
            System.out.print("Enter command: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    System.out.print("Document name: ");
                    String newDoc = scanner.nextLine();
                    fileSystem.createDocument(newDoc);
                    System.out.println("Created document: " + newDoc);
                    break;
                case "2":
                    System.out.print("Document name: ");
                    String docToOpen = scanner.nextLine();
                    currentDocument = docToOpen;
                    String content = fileSystem.openDocument(docToOpen);
                    System.out.println("Opened document:\n" + content);
                    break;
                case "3":
                    if (currentDocument == null) {
                        System.out.println("No document open.");
                        break;
                    }
                    System.out.println("Enter new content (end with an empty line):");
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while (!(line = scanner.nextLine()).isEmpty()) {
                        sb.append(line).append("\n");
                    }
                    lastEditedContent = sb.toString();
                    fileSystem.updateDocument(currentDocument, lastEditedContent, currentUsername);

                    break;
                case "4":
                    System.out.print("Folder name: ");
                    String folderToSwitch = scanner.nextLine();
                    fileSystem.attachObserver(folderToSwitch, observer);
                    System.out.println("Switched to folder .");
                    break;
                case "5":
                    System.out.print("Folder name: ");
                    String folder = scanner.nextLine();
                    System.out.print("Username to share with: ");
                    String shareTo = scanner.nextLine();
                    fileSystem.shareFolder(folder, shareTo);
                    System.out.println("Shared folder " + folder + " with " + shareTo);
                    break;
                case "6":
                    System.out.println("Exiting.");
                    fileSystem.updateDocument(currentDocument, lastEditedContent, currentUsername);
                    System.exit(0);
                default:
                    System.out.println("Unknown command.");
            }
        }
    }


}
