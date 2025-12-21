package virtualcare.gui;

import virtualcare.service.AuthenticationService;
import virtualcare.service.DataManager;
import virtualcare.model.User;
import virtualcare.service.AuthenticationService.UserType;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Login Panel for user authentication
 */
public class LoginPanel extends JPanel {
    private DataManager dataManager;
    private AuthenticationService authService;
    private JTextField userIDField;
    private JPasswordField passwordField;
    private JComboBox<UserType> userTypeCombo;
    private MainGUI mainGUI;

    public LoginPanel(DataManager dataManager, AuthenticationService authService, MainGUI mainGUI) {
        this.dataManager = dataManager;
        this.authService = authService;
        this.mainGUI = mainGUI;
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));

        // Title
        JLabel titleLabel = new JLabel("VirtualCare 2025", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 30, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Login Form
        JPanel loginFormPanel = createLoginForm();
        add(loginFormPanel, BorderLayout.CENTER);

        // Registration link
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(new Color(240, 248, 255));
        JLabel registerLabel = new JLabel("New user? Click here to register");
        registerLabel.setForeground(new Color(0, 102, 204));
        registerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showRegistrationDialog();
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerLabel.setText("<html><u>New user? Click here to register</u></html>");
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerLabel.setText("New user? Click here to register");
            }
        });
        bottomPanel.add(registerLabel);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createLoginForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Login",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16)
        ));
        panel.setBackground(new Color(255, 255, 255));
        panel.setPreferredSize(new Dimension(400, 300));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // User Type
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("User Type:"), gbc);
        gbc.gridx = 1;
        userTypeCombo = new JComboBox<>(UserType.values());
        userTypeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    UserType type = (UserType) value;
                    switch (type) {
                        case PATIENT:
                            setText("Patient");
                            break;
                        case PROVIDER:
                            setText("Provider");
                            break;
                        case ADMIN:
                            setText("Admin");
                            break;
                    }
                }
                return this;
            }
        });
        panel.add(userTypeCombo, gbc);

        // User ID
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("User ID:"), gbc);
        gbc.gridx = 1;
        userIDField = new JTextField(20);
        panel.add(userIDField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        // Login Button
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setPreferredSize(new Dimension(150, 35));
        loginBtn.addActionListener(e -> performLogin());
        
        // Allow Enter key to trigger login
        passwordField.addActionListener(e -> performLogin());
        
        panel.add(loginBtn, gbc);

        // Center the panel
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(240, 248, 255));
        wrapper.add(panel);
        return wrapper;
    }

    private void performLogin() {
        String userID = userIDField.getText().trim();
        String password = new String(passwordField.getPassword());
        UserType userType = (UserType) userTypeCombo.getSelectedItem();

        if (userID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter User ID!", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter password!", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = authService.authenticate(userID, password, userType);

        if (user != null) {
            // Successful login
            passwordField.setText("");
            mainGUI.onSuccessfulLogin(userType);
        } else {
            // Failed login
            JOptionPane.showMessageDialog(
                this,
                "Invalid credentials. Please check your User ID and password.",
                "Login Failed",
                JOptionPane.ERROR_MESSAGE
            );
            passwordField.setText("");
        }
    }

    private void showRegistrationDialog() {
        JDialog registrationDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Register New User", true);
        registrationDialog.setSize(500, 400);
        registrationDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // User Type
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("User Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<UserType> typeCombo = new JComboBox<>(UserType.values());
        typeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    UserType type = (UserType) value;
                    switch (type) {
                        case PATIENT:
                            setText("Patient");
                            break;
                        case PROVIDER:
                            setText("Provider");
                            break;
                        case ADMIN:
                            setText("Admin");
                            break;
                    }
                }
                return this;
            }
        });
        panel.add(typeCombo, gbc);

        // Name
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        panel.add(nameField, gbc);

        // User ID (generated for patients, manual for providers/admins)
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("User ID:"), gbc);
        gbc.gridx = 1;
        JTextField userIDFieldReg = new JTextField(20);
        panel.add(userIDFieldReg, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField passwordFieldReg = new JPasswordField(20);
        panel.add(passwordFieldReg, gbc);

        // Additional fields based on user type
        JTextField contactField = new JTextField(20);
        JTextArea medicalHistoryField = new JTextArea(3, 20);
        medicalHistoryField.setLineWrap(true);
        JTextField specialtyField = new JTextField(20);
        JTextField availabilityField = new JTextField(20);

        Runnable updateFields = () -> {
            UserType selectedType = (UserType) typeCombo.getSelectedItem();
            // Remove existing dynamic fields
            // For simplicity, we'll add fields conditionally in the dialog
        };

        typeCombo.addActionListener(e -> {
            UserType selectedType = (UserType) typeCombo.getSelectedItem();
            if (selectedType == UserType.PATIENT) {
                userIDFieldReg.setEditable(false);
                userIDFieldReg.setText("(Will be auto-generated)");
            } else {
                userIDFieldReg.setEditable(true);
                userIDFieldReg.setText("");
            }
        });

        // Register Button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton registerBtn = new JButton("Register");
        registerBtn.addActionListener(e -> {
            UserType selectedType = (UserType) typeCombo.getSelectedItem();
            String name = nameField.getText().trim();
            String userID = userIDFieldReg.getText().trim();
            String password = new String(passwordFieldReg.getPassword());

            if (name.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(registrationDialog, "Please fill in all required fields!");
                return;
            }

            try {
                if (selectedType == UserType.PATIENT) {
                    userID = dataManager.generatePatientID();
                    String contact = JOptionPane.showInputDialog(registrationDialog, "Enter contact information:");
                    String medicalHistory = JOptionPane.showInputDialog(registrationDialog, "Enter medical history (optional):");
                    contact = (contact != null) ? contact : "";
                    medicalHistory = (medicalHistory != null) ? medicalHistory : "";
                    
                    virtualcare.model.Patient patient = new virtualcare.model.Patient(userID, name, contact, medicalHistory, password);
                    patient.register();
                    dataManager.savePatient(patient);
                    JOptionPane.showMessageDialog(registrationDialog, "Patient registered successfully! Your User ID is: " + userID);
                } else if (selectedType == UserType.PROVIDER) {
                    if (userID.isEmpty()) {
                        userID = dataManager.generateProviderID();
                    }
                    String specialty = JOptionPane.showInputDialog(registrationDialog, "Enter specialty:");
                    String availability = JOptionPane.showInputDialog(registrationDialog, "Enter availability:");
                    specialty = (specialty != null) ? specialty : "";
                    availability = (availability != null) ? availability : "";
                    
                    virtualcare.model.Provider provider = new virtualcare.model.Provider(userID, name, specialty, availability, password);
                    dataManager.saveProvider(provider);
                    JOptionPane.showMessageDialog(registrationDialog, "Provider registered successfully! Your User ID is: " + userID);
                } else if (selectedType == UserType.ADMIN) {
                    if (userID.isEmpty()) {
                        userID = dataManager.generateAdminID();
                    }
                    virtualcare.model.Admin admin = new virtualcare.model.Admin(userID, name, password);
                    dataManager.saveAdmin(admin);
                    JOptionPane.showMessageDialog(registrationDialog, "Admin registered successfully! Your User ID is: " + userID);
                }
                registrationDialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(registrationDialog, "Registration failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(registerBtn, gbc);

        registrationDialog.add(panel);
        registrationDialog.setVisible(true);
    }
}

