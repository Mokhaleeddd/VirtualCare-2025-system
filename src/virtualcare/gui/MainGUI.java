package virtualcare.gui;

import virtualcare.service.DataManager;
import virtualcare.service.AuthenticationService;
import virtualcare.service.AuthenticationService.UserType;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class MainGUI extends JFrame {
    private DataManager dataManager;
    private AuthenticationService authService;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private PatientPanel patientPanel;
    private ProviderPanel providerPanel;
    private AdminPanel adminPanel;

    public MainGUI() {
        dataManager = new DataManager();
        authService = new AuthenticationService(dataManager);
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("VirtualCare 2025 - Healthcare Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create login panel with authentication
        loginPanel = new LoginPanel(dataManager, authService, this);
        mainPanel.add(loginPanel, "LOGIN");

        // Create user panels
        patientPanel = new PatientPanel(dataManager, authService);
        providerPanel = new ProviderPanel(dataManager, authService);
        adminPanel = new AdminPanel(dataManager);

        mainPanel.add(patientPanel, "PATIENT");
        mainPanel.add(providerPanel, "PROVIDER");
        mainPanel.add(adminPanel, "ADMIN");

        add(mainPanel);
        
        // Show login panel first
        cardLayout.show(mainPanel, "LOGIN");
    }
    
    public void showMainMenu() {
        authService.logout();
        cardLayout.show(mainPanel, "LOGIN");
    }

    /**
     * Called when user successfully logs in
     * Routes to appropriate panel based on user type
     */
    public void onSuccessfulLogin(UserType userType) {
        switch (userType) {
            case PATIENT:
                patientPanel.refreshPanel();
                cardLayout.show(mainPanel, "PATIENT");
                break;
            case PROVIDER:
                providerPanel.refreshPanel();
                cardLayout.show(mainPanel, "PROVIDER");
                break;
            case ADMIN:
                cardLayout.show(mainPanel, "ADMIN");
                break;
        }
    }
    
    public AuthenticationService getAuthService() {
        return authService;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainGUI().setVisible(true);
        });
    }
}

