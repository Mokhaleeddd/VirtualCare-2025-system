package virtualcare.gui;

import virtualcare.model.*;
import virtualcare.service.DataManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Admin Dashboard Panel
 * Handles administrative operations
 */
public class AdminPanel extends JPanel {
    private DataManager dataManager;
    private JTable providerTable, billTable, feedbackTable;
    private DefaultTableModel providerTableModel, billTableModel, feedbackTableModel;
    private JTextField providerNameField, availabilityField;
    private JComboBox<String> specialtyField;

    public AdminPanel(DataManager dataManager) {
        this.dataManager = dataManager;
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Admin Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Configure Providers", createProviderConfigPanel());
        tabbedPane.addTab("Generate Bill", createBillPanel());
        tabbedPane.addTab("View Providers", createProvidersListPanel());
        tabbedPane.addTab("View Bills", createBillsListPanel());
        tabbedPane.addTab("View Feedback", createFeedbackListPanel());

        add(tabbedPane, BorderLayout.CENTER);
        

        JButton backBtn = new JButton("Back to Main Menu");
        backBtn.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (frame instanceof MainGUI) {
                ((MainGUI) frame).showMainMenu();
            }
        });
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createProviderConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Admin:"), gbc);
        gbc.gridx = 1;
        JComboBox<Admin> adminCombo = new JComboBox<>();
        try {
            List<Admin> admins = dataManager.getAllAdmins();
            for (Admin a : admins) {
                adminCombo.addItem(a);
            }
        } catch (Exception ex) {

        }
        adminCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Admin) {
                    setText(((Admin) value).getName());
                }
                return this;
            }
        });
        panel.add(adminCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Provider Name:"), gbc);
        gbc.gridx = 1;
        providerNameField = new JTextField(20);
        panel.add(providerNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Specialty:"), gbc);
        gbc.gridx = 1;
        specialtyField = new JComboBox<String>();
        specialtyField.addItem(null); // Add null option for "select specialty"
        String[] specialties = {
            "Cardiology",
            "Dermatology",
            "Endocrinology",
            "Gastroenterology",
            "General Practice",
            "Internal Medicine",
            "Neurology",
            "Oncology",
            "Orthopedics",
            "Pediatrics",
            "Psychiatry",
            "Pulmonology",
            "Radiology",
            "Surgery",
            "Urology"
        };
        for (String specialty : specialties) {
            specialtyField.addItem(specialty);
        }
        specialtyField.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("-- Select Specialty --");
                } else {
                    setText(value.toString());
                }
                return this;
            }
        });
        panel.add(specialtyField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Availability:"), gbc);
        gbc.gridx = 1;
        availabilityField = new JTextField(20);
        panel.add(availabilityField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton createBtn = new JButton("Create Provider Account");
        createBtn.addActionListener(e -> {
            try {
                Admin admin = (Admin) adminCombo.getSelectedItem();
                if (admin == null) {

                    String adminID = JOptionPane.showInputDialog(this, "Enter Admin ID:");
                    if (adminID != null && !adminID.isEmpty()) {
                        admin = new Admin(adminID, "Admin");
                        dataManager.saveAdmin(admin);
                        adminCombo.addItem(admin);
                        adminCombo.setSelectedItem(admin);
                    } else {
                        JOptionPane.showMessageDialog(this, "Please select or create an admin!");
                        return;
                    }
                }
                String specialty = (String) specialtyField.getSelectedItem();
                if (!providerNameField.getText().isEmpty() && specialty != null) {
                    String providerID = dataManager.generateProviderID();
                    admin.createProviderAccount(
                        providerID,
                        providerNameField.getText(),
                        specialty,
                        availabilityField.getText()
                    );
                    Provider provider = new Provider(
                        providerID,
                        providerNameField.getText(),
                        specialty,
                        availabilityField.getText()
                    );
                    provider.setManagedBy(admin);
                    dataManager.saveProvider(provider);
                    admin.manageSystem();
                    dataManager.saveAdmin(admin);
                    JOptionPane.showMessageDialog(this, "Provider account created successfully! ID: " + providerID);
                    providerNameField.setText("");
                    specialtyField.setSelectedItem(null);
                    availabilityField.setText("");
                } else {
                    if (providerNameField.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Please enter provider name!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Please select a specialty!");
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        panel.add(createBtn, gbc);

        return panel;
    }

    private JPanel createBillPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Admin:"), gbc);
        gbc.gridx = 1;
        JComboBox<Admin> adminCombo = new JComboBox<>();
        try {
            List<Admin> admins = dataManager.getAllAdmins();
            for (Admin a : admins) {
                adminCombo.addItem(a);
            }
        } catch (Exception ex) {

        }
        adminCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Admin) {
                    setText(((Admin) value).getName());
                }
                return this;
            }
        });
        panel.add(adminCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Appointment:"), gbc);
        gbc.gridx = 1;
        JComboBox<Appointment> appointmentCombo = new JComboBox<>();
        try {
            List<Appointment> appointments = dataManager.getAllAppointments();
            for (Appointment apt : appointments) {
                if (apt.getBill() == null) {
                    appointmentCombo.addItem(apt);
                }
            }
        } catch (Exception ex) {

        }
        appointmentCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Appointment) {
                    Appointment apt = (Appointment) value;
                    String patientName = apt.getPatient() != null ? apt.getPatient().getName() : "N/A";
                    setText(patientName + " - " + apt.getDateTime());
                }
                return this;
            }
        });
        panel.add(appointmentCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        JTextField amountField = new JTextField(20);
        panel.add(amountField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Payment Method:"), gbc);
        gbc.gridx = 1;
        JTextField paymentMethodField = new JTextField(20);
        panel.add(paymentMethodField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton generateBtn = new JButton("Generate Bill");
        generateBtn.addActionListener(e -> {
            try {
                Appointment appointment = (Appointment) appointmentCombo.getSelectedItem();
                if (appointment != null && !amountField.getText().isEmpty()) {
                    String billID = dataManager.generateBillID(appointment.getAppointmentID());
                    Bill bill = new Bill(
                        billID,
                        Double.parseDouble(amountField.getText()),
                        "Pending",
                        paymentMethodField.getText()
                    );
                    bill.setAppointment(appointment);
                    appointment.setBill(bill);
                    bill.generate();
                    dataManager.saveBill(bill);
                    dataManager.saveAppointment(appointment);
                    JOptionPane.showMessageDialog(this, "Bill generated successfully!");

                    appointmentCombo.removeAllItems();
                    List<Appointment> appointments = dataManager.getAllAppointments();
                    for (Appointment apt : appointments) {
                        if (apt.getBill() == null) {
                            appointmentCombo.addItem(apt);
                        }
                    }
                    amountField.setText("");
                    paymentMethodField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Please select an appointment and enter amount!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        panel.add(generateBtn, gbc);

        return panel;
    }

    private JPanel createProvidersListPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"Provider ID", "Name", "Specialty", "Availability"};
        providerTableModel = new DefaultTableModel(columnNames, 0);
        providerTable = new JTable(providerTableModel);
        JScrollPane scrollPane = new JScrollPane(providerTable);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshBtn = new JButton("Refresh Providers");
        refreshBtn.addActionListener(e -> {
            try {
                List<Provider> providers = dataManager.getAllProviders();
                providerTableModel.setRowCount(0);
                for (Provider p : providers) {
                    providerTableModel.addRow(new Object[]{
                        p.getUserID(),
                        p.getName(),
                        p.getSpecialty(),
                        p.getAvailability()
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        buttonPanel.add(refreshBtn);

        JButton deleteBtn = new JButton("Delete Selected Provider");
        deleteBtn.addActionListener(e -> {
            int selectedRow = providerTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a provider to delete!");
                return;
            }
            
            String providerID = (String) providerTableModel.getValueAt(selectedRow, 0);
            String providerName = (String) providerTableModel.getValueAt(selectedRow, 1);
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete provider " + providerName + " (ID: " + providerID + ")?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    dataManager.deleteProvider(providerID);
                    JOptionPane.showMessageDialog(this, "Provider deleted successfully!");
                    
                    // Refresh the table
                    List<Provider> providers = dataManager.getAllProviders();
                    providerTableModel.setRowCount(0);
                    for (Provider p : providers) {
                        providerTableModel.addRow(new Object[]{
                            p.getUserID(),
                            p.getName(),
                            p.getSpecialty(),
                            p.getAvailability()
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting provider: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        buttonPanel.add(deleteBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBillsListPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"Bill ID", "Amount", "Status", "Payment Method"};
        billTableModel = new DefaultTableModel(columnNames, 0);
        billTable = new JTable(billTableModel);
        JScrollPane scrollPane = new JScrollPane(billTable);

        JButton refreshBtn = new JButton("Refresh Bills");
        refreshBtn.addActionListener(e -> {
            try {
                List<Bill> bills = dataManager.getAllBills();
                billTableModel.setRowCount(0);
                for (Bill b : bills) {
                    billTableModel.addRow(new Object[]{
                        b.getBillID(),
                        b.getAmount(),
                        b.getStatus(),
                        b.getPaymentMethod()
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFeedbackListPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"Feedback ID", "Rating", "Comment", "Date"};
        feedbackTableModel = new DefaultTableModel(columnNames, 0);
        feedbackTable = new JTable(feedbackTableModel);
        JScrollPane scrollPane = new JScrollPane(feedbackTable);

        JButton refreshBtn = new JButton("Refresh Feedback");
        refreshBtn.addActionListener(e -> {
            try {
                List<Feedback> feedbacks = dataManager.getAllFeedbacks();
                feedbackTableModel.setRowCount(0);
                for (Feedback f : feedbacks) {
                    feedbackTableModel.addRow(new Object[]{
                        f.getFeedbackID(),
                        f.getRating(),
                        f.getComment(),
                        f.getDate()
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);

        return panel;
    }
}

