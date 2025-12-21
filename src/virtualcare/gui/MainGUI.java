package virtualcare.gui;

import virtualcare.service.DataManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class MainGUI extends JFrame {
    private DataManager dataManager;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainGUI() {
        dataManager = new DataManager();
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("VirtualCare 2025 - Healthcare Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel loginPanel = createLoginPanel();
        mainPanel.add(loginPanel, "LOGIN");

        PatientPanel patientPanel = new PatientPanel(dataManager);
        ProviderPanel providerPanel = new ProviderPanel(dataManager);
        AdminPanel adminPanel = new AdminPanel(dataManager);

        mainPanel.add(patientPanel, "PATIENT");
        mainPanel.add(providerPanel, "PROVIDER");
        mainPanel.add(adminPanel, "ADMIN");

        add(mainPanel);
    }
    
    public void showMainMenu() {
        cardLayout.show(mainPanel, "LOGIN");
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        JLabel titleLabel = new JLabel("VirtualCare 2025", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 30, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 200, 50, 200));

        JButton patientBtn = new JButton("Patient Interface");
        patientBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        patientBtn.setPreferredSize(new Dimension(200, 60));
        patientBtn.addActionListener(e -> cardLayout.show(mainPanel, "PATIENT"));

        JButton providerBtn = new JButton("Provider Interface");
        providerBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        providerBtn.setPreferredSize(new Dimension(200, 60));
        providerBtn.addActionListener(e -> cardLayout.show(mainPanel, "PROVIDER"));

        JButton adminBtn = new JButton("Admin Dashboard");
        adminBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        adminBtn.setPreferredSize(new Dimension(200, 60));
        adminBtn.addActionListener(e -> cardLayout.show(mainPanel, "ADMIN"));

        buttonPanel.add(patientBtn);
        buttonPanel.add(providerBtn);
        buttonPanel.add(adminBtn);

        panel.add(buttonPanel, BorderLayout.CENTER);

        return panel;
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

