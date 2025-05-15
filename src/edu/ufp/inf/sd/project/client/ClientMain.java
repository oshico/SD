package edu.ufp.inf.sd.project.client;

import edu.ufp.inf.sd.project.server.AuthServiceRI;
import edu.ufp.inf.sd.project.server.SessionRI;
import edu.ufp.inf.sd.project.server.SubjectFileSystemRI;
import edu.ufp.inf.sd.project.util.SetupContextRMI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ClientMain {
    // RMI setup
    private Registry registry;
    private AuthServiceRI authService;
    private SessionRI session;
    private SubjectFileSystemRI fileSystem;
    private ObserverRI observer;

    // Client state
    private String currentUsername;
    private String currentDocument;
    private boolean isEditing = false;
    private Timer autoSaveTimer;

    // GUI components
    private JFrame mainFrame;
    private JTextArea documentTextArea;
    private JList<String> documentsList;
    private DefaultListModel<String> documentsModel;
    private JLabel statusLabel;

    /**
     * Constructor for the client main class.
     */
    public ClientMain() {
        // Initialize GUI first
        createGUI();
    }

    /**
     * Main method to start the client.
     *
     * @param args Command line arguments (registry host, registry port, service name)
     */
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

        // Show login dialog
        client.showLoginDialog();
    }

    /**
     * Looks up the remote service from the registry.
     *
     * @param registryHost The registry host
     * @param registryPort The registry port
     * @param serviceName  The service name
     */
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

        } catch (RemoteException | NotBoundException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            JOptionPane.showMessageDialog(mainFrame,
                    "Could not connect to server: " + e.getMessage(),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    /**
     * Creates the GUI for the client.
     */
    private void createGUI() {
        // Create main frame
        mainFrame = new JFrame("Collaborative Editor");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);

        // Create main panel with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // Create left panel with documents list
        JPanel leftPanel = new JPanel(new BorderLayout());
        documentsModel = new DefaultListModel<>();
        documentsList = new JList<>(documentsModel);
        documentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        documentsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && documentsList.getSelectedValue() != null) {
                openDocument(documentsList.getSelectedValue());
            }
        });

        leftPanel.add(new JScrollPane(documentsList), BorderLayout.CENTER);

        // Add buttons for document management
        JPanel buttonPanel = new JPanel();
        JButton newDocButton = new JButton("New Document");
        newDocButton.addActionListener(e -> showNewDocumentDialog());
        buttonPanel.add(newDocButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshDocumentsList());
        buttonPanel.add(refreshButton);

        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Create right panel with document editor
        JPanel rightPanel = new JPanel(new BorderLayout());
        documentTextArea = new JTextArea();
        documentTextArea.setEditable(false);
        documentTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (isEditing) {
                    documentChanged();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (isEditing) {
                    documentChanged();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (isEditing) {
                    documentChanged();
                }
            }
        });

        rightPanel.add(new JScrollPane(documentTextArea), BorderLayout.CENTER);

        // Add status bar
        statusLabel = new JLabel("Not logged in");
        rightPanel.add(statusLabel, BorderLayout.SOUTH);

        // Add panels to split pane
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        splitPane.setDividerLocation(200);

        // Add menu bar
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem loginItem = new JMenuItem("Login");
        loginItem.addActionListener(e -> showLoginDialog());
        fileMenu.add(loginItem);

        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> logout());
        fileMenu.add(logoutItem);

        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> {
            logout();
            System.exit(0);
        });
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);

        // Add components to frame
        mainFrame.setJMenuBar(menuBar);
        mainFrame.getContentPane().add(splitPane);

        // Show frame
        mainFrame.setVisible(true);
    }

    /**
     * Shows the login dialog.
     */
    private void showLoginDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2));

        JTextField usernameField = new JTextField(10);
        JPasswordField passwordField = new JPasswordField(10);

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        JCheckBox registerCheck = new JCheckBox("Register new user");
        panel.add(registerCheck);

        Object[] options = {"Login", "Cancel"};

        int result = JOptionPane.showOptionDialog(
                mainFrame,
                panel,
                "Login",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame,
                        "Username and password cannot be empty",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (registerCheck.isSelected()) {
                    // Register new user
                    boolean registered = authService.register(username, password);
                    if (registered) {
                        JOptionPane.showMessageDialog(mainFrame,
                                "Registration successful. Please login.",
                                "Registration",
                                JOptionPane.INFORMATION_MESSAGE);

                        // Show login dialog again
                        showLoginDialog();
                    } else {
                        JOptionPane.showMessageDialog(mainFrame,
                                "Registration failed. Username may already exist.",
                                "Registration Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Login
                    session = authService.login(username, password);

                    if (session != null) {
                        currentUsername = username;
                        fileSystem = session.getFileSystem();

                        // Create and register observer
                        observer = new ObserverImpl(this);

                        // Update UI
                        statusLabel.setText("Logged in as: " + currentUsername);
                        refreshDocumentsList();

                        JOptionPane.showMessageDialog(mainFrame,
                                "Login successful!",
                                "Login",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(mainFrame,
                                "Login failed. Invalid username or password.",
                                "Login Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(mainFrame,
                        "Error communicating with server: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Logs out the current user.
     */
    private void logout() {
        try {
            if (session != null) {
                // Stop auto save timer
                if (autoSaveTimer != null) {
                    autoSaveTimer.cancel();
                    autoSaveTimer = null;
                }

                // Detach observer from current document
                if (currentDocument != null && fileSystem != null) {
                    fileSystem.detachObserver(currentDocument, observer);
                }

                // Logout session
                session.logout();
                session = null;
                fileSystem = null;
                observer = null;

                // Clear UI
                currentUsername = null;
                currentDocument = null;
                documentsModel.clear();
                documentTextArea.setText("");
                documentTextArea.setEditable(false);
                statusLabel.setText("Not logged in");

                JOptionPane.showMessageDialog(mainFrame,
                        "Logged out successfully",
                        "Logout",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (RemoteException e) {
            System.err.println("Error during logout: " + e.getMessage());
        }
    }

    /**
     * Refreshes the list of documents.
     */
    private void refreshDocumentsList() {
        if (fileSystem == null) {
            return;
        }

        try {
            // Get list of documents
            List<String> documents = fileSystem.listDocuments();

            // Update UI
            documentsModel.clear();
            for (String doc : documents) {
                documentsModel.addElement(doc);
            }

            // Select current document if it exists
            if (currentDocument != null) {
                documentsList.setSelectedValue(currentDocument, true);
            }

        } catch (RemoteException e) {
            System.err.println("Error refreshing documents list: " + e.getMessage());
            JOptionPane.showMessageDialog(mainFrame,
                    "Error refreshing documents list: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Shows the dialog for creating a new document.
     */
    private void showNewDocumentDialog() {
        if (fileSystem == null) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Please login first",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String docName = JOptionPane.showInputDialog(mainFrame,
                "Enter new document name:",
                "New Document",
                JOptionPane.PLAIN_MESSAGE);

        if (docName != null && !docName.trim().isEmpty()) {
            try {
                boolean created = fileSystem.createDocument(docName.trim());

                if (created) {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Document created successfully",
                            "New Document",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Refresh list and open the new document
                    refreshDocumentsList();
                    openDocument(docName.trim());
                } else {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Failed to create document. Name may already exist.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (RemoteException e) {
                System.err.println("Error creating document: " + e.getMessage());
                JOptionPane.showMessageDialog(mainFrame,
                        "Error creating document: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Opens a document for editing.
     *
     * @param documentName The name of the document to open
     */
    private void openDocument(String documentName) {
        if (fileSystem == null) {
            return;
        }

        try {
            // Detach observer from current document if there is one
            if (currentDocument != null && observer != null) {
                fileSystem.detachObserver(currentDocument, observer);
            }

            // Cancel auto-save timer if it exists
            if (autoSaveTimer != null) {
                autoSaveTimer.cancel();
                autoSaveTimer = null;
            }

            // Open the document
            String content = fileSystem.openDocument(documentName);

            // Update state and UI
            currentDocument = documentName;
            isEditing = false;  // Temporarily disable editing to prevent triggering change events
            documentTextArea.setText(content);
            documentTextArea.setEditable(true);
            isEditing = true;   // Re-enable editing
            statusLabel.setText("Editing: " + documentName + " as " + currentUsername);

            // Attach observer to the document
            fileSystem.attachObserver(documentName, observer);

            // Setup auto-save timer
            autoSaveTimer = new Timer();
            autoSaveTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    saveDocument();
                }
            }, 5000, 5000);  // Auto-save every 5 seconds

        } catch (RemoteException e) {
            System.err.println("Error opening document: " + e.getMessage());
            JOptionPane.showMessageDialog(mainFrame,
                    "Error opening document: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles document content changes.
     */
    private void documentChanged() {
        // Update status to show unsaved changes
        statusLabel.setText("Editing: " + currentDocument + " as " + currentUsername + " (Unsaved changes)");
    }

    /**
     * Saves the current document.
     */
    private void saveDocument() {
        if (fileSystem == null || currentDocument == null) {
            return;
        }

        try {
            // Get current content
            String content = documentTextArea.getText();

            // Save document
            boolean saved = fileSystem.saveDocument(currentDocument, content);

            if (saved) {
                // Update status
                statusLabel.setText("Editing: " + currentDocument + " as " + currentUsername + " (Saved)");
            } else {
                System.err.println("Failed to save document");
                statusLabel.setText("Editing: " + currentDocument + " as " + currentUsername + " (Save failed)");
            }

        } catch (RemoteException e) {
            System.err.println("Error saving document: " + e.getMessage());
            statusLabel.setText("Editing: " + currentDocument + " as " + currentUsername + " (Save error)");
        }
    }

    /**
     * Updates the document content when notified by the observer.
     *
     * @param content The new content
     */
    public void updateDocumentContent(String content) {
        // Update document content without triggering change events
        isEditing = false;
        documentTextArea.setText(content);
        isEditing = true;

        // Update status
        statusLabel.setText("Editing: " + currentDocument + " as " + currentUsername + " (Updated by others)");
    }

    /**
     * Gets the current username.
     *
     * @return The current username
     */
    public String getCurrentUsername() {
        return currentUsername;
    }

    /**
     * Gets the current document name.
     *
     * @return The current document name
     */
    public String getCurrentDocument() {
        return currentDocument;
    }
}
