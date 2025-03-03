package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.cdimascio.dotenv.Dotenv;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class NapsterClientUI {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private RemoteService service;
    private String username;
    private Thread serverThread;
    private static final Dotenv dotenv = Dotenv.load();
    private static final String SENT_DIR = dotenv.get("SENT_DIR");

    public NapsterClientUI() {
        try {
            Registry registry = LocateRegistry.getRegistry("192.168.1.101", 1099);
            service = (RemoteService) registry.lookup("RemoteService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error connecting to server: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        frame = new JFrame("Napster Client");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(450, 350);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLoginPanel(), "Login");
        mainPanel.add(createRegisterPanel(), "Register");
        mainPanel.add(createMainPanel(), "Main");

        frame.add(mainPanel);
        // Handle closing event
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (serverThread != null && serverThread.isAlive()) {
                    serverThread.interrupt();  // Stop the thread
                }
                System.exit(0);
            }
        });
        frame.setVisible(true);
    }

    private void startPingThread() {
        Thread pingThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5 * 60 * 1000); // 5 minutes
                    if (service != null) {
                        service.ping(username,InetAddress.getLocalHost().getHostAddress());
                        System.out.println("Ping sent to server");
                    }
                } catch (Exception e) {
                    System.out.println("Ping failed: " + e.getMessage());
                }
            }
        });
        pingThread.setDaemon(true); // Ensures it stops when the application exits
        pingThread.start();
    }


    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; panel.add(usernameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; panel.add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(loginButton, gbc);
        gbc.gridx = 1; panel.add(registerButton, gbc);


        loginButton.addActionListener(e -> {
            try {
                String response = service.login(usernameField.getText(), new String(passwordField.getPassword()), InetAddress.getLocalHost().getHostAddress());
                if (response.contains("true")) {
                    cardLayout.show(mainPanel, "Main");
                    serverThread = new Thread(() -> {
                        try {
                            serveurfile.serverfile(InetAddress.getLocalHost().getHostAddress());

                        } catch (UnknownHostException ex) {

                        }
                    });
                    serverThread.start();
                    startPingThread();
                    username = usernameField.getText();
                } else {
                    JOptionPane.showMessageDialog(frame, "Login failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(e -> cardLayout.show(mainPanel, "Register"));

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back");

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; panel.add(usernameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; panel.add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(registerButton, gbc);
        gbc.gridx = 1; panel.add(backButton, gbc);

        registerButton.addActionListener(e -> {
            try {
                boolean emptyFields = !usernameField.getText().isEmpty() && !passwordField.getText().isEmpty();
                if (emptyFields) {
                    String response = service.signUp(usernameField.getText(), new String(passwordField.getPassword()), 5000, service.getip());
                    if (response.contains("true")) {
                        JOptionPane.showMessageDialog(frame, "Registration Successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                        cardLayout.show(mainPanel, "Login");
                    }else{
                        JOptionPane.showMessageDialog(frame, "Registration Failed", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Registration Failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Login"));

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout());
        JTextField songField = new JTextField(20);
        JButton searchButton = new JButton("Search Song");
        JButton addButton = new JButton("Add Song");

        topPanel.add(songField);
        topPanel.add(searchButton);
        topPanel.add(addButton);

//        JTextArea resultsArea = new JTextArea(10, 30);
//        resultsArea.setEditable(false);


        // Define table columns based on server's JSON response
        String[] columns = {"Filename", "Size", "IP Address", "Port"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Check for double-click
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) { // Ensure a row is selected
                        String filename = model.getValueAt(selectedRow, 0).toString();
                        String ip = model.getValueAt(selectedRow, 2).toString();
                        String port = model.getValueAt(selectedRow, 3).toString();

                        // Call function when row is double-clicked
                        Clientfile.requestFile(ip, Integer.parseInt(port), filename);
                    }
                }
            }
        });


        searchButton.addActionListener(e -> {
            try {
                String searchTerm = songField.getText().toLowerCase();
                String response = service.findfile(searchTerm);
                model.setRowCount(0); // Clear existing data

                // Parse JSON response
                Gson gson = new Gson();
                JsonObject[] results = gson.fromJson(response, JsonObject[].class);

                // Add rows to the table
                for (JsonObject obj : results) {
                    String filename = obj.get("Filename").getAsString();
                    String size = obj.get("taille").getAsString();
                    String ip = obj.get("ip").getAsString();
                    String port = obj.get("portenv").getAsString();

                    model.addRow(new Object[]{
                            filename, // Filename from search term
                            size,
                            ip,
                            port
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        addButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(frame);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                try {
                    File selectedFile = fileChooser.getSelectedFile();
                    String fileName = selectedFile.getName();
                    assert SENT_DIR != null;
                    File destinationFolder = new File(SENT_DIR);
                    if (!destinationFolder.exists()) destinationFolder.mkdirs();
                    Files.copy(selectedFile.toPath(), new File(destinationFolder, fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    String response = service.publish(username, fileName, (int) selectedFile.length());
                    JOptionPane.showMessageDialog(frame, response, "Add Song", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(NapsterClientUI::new);
    }
}
