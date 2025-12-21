package virtualcare.gui;

import virtualcare.model.*;
import virtualcare.service.DataManager;
import virtualcare.service.AuthenticationService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ProviderPanel extends JPanel {
    private DataManager dataManager;
    private AuthenticationService authService;
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, specialtyField, availabilityField;
    private JTextField medicationField, dosageField;

    public ProviderPanel(DataManager dataManager, AuthenticationService authService) {
        this.dataManager = dataManager;
        this.authService = authService;
        initializePanel();
    }
    
    /**
     * Refreshes the panel - reloads provider data
     * Call this when switching to this panel after login
     */
    public void refreshPanel() {
        // Remove all components and reinitialize
        removeAll();
        initializePanel();
        revalidate();
        repaint();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Provider Interface", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Manage Schedule", createSchedulePanel());
        tabbedPane.addTab("Conduct Consultation", createConsultationPanel());
        tabbedPane.addTab("Issue Prescription", createPrescriptionPanel());
        tabbedPane.addTab("My Appointments", createAppointmentsPanel());

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

    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Provider:"), gbc);
        gbc.gridx = 1;
        JComboBox<Provider> providerCombo = new JComboBox<>();
        try {
            List<Provider> providers = dataManager.getAllProviders();
            if (providers == null || providers.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No providers found in the system.");
            } else {
                for (Provider p : providers) {
                    if (p != null) {
                        providerCombo.addItem(p);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading providers: " + ex.getMessage());
            ex.printStackTrace();
        }
        providerCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Provider) {
                    setText(((Provider) value).getName() + " - " + ((Provider) value).getSpecialty());
                }
                return this;
            }
        });
        panel.add(providerCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        nameField.setEditable(false);
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Specialty:"), gbc);
        gbc.gridx = 1;
        specialtyField = new JTextField(20);
        specialtyField.setEditable(false);
        panel.add(specialtyField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Availability:"), gbc);
        gbc.gridx = 1;
        availabilityField = new JTextField(20);
        panel.add(availabilityField, gbc);

        providerCombo.addActionListener(e -> {
            Provider selected = (Provider) providerCombo.getSelectedItem();
            if (selected != null) {
                nameField.setText(selected.getName());
                specialtyField.setText(selected.getSpecialty());
                availabilityField.setText(selected.getAvailability() != null ? selected.getAvailability() : "");
            } else {
                nameField.setText("");
                specialtyField.setText("");
                availabilityField.setText("");
            }
        });

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton updateBtn = new JButton("Update Availability");
        updateBtn.addActionListener(e -> {
            try {
                Provider provider = (Provider) providerCombo.getSelectedItem();
                if (provider == null) {
                    JOptionPane.showMessageDialog(this, "Please select a provider!");
                    return;
                }
                if (availabilityField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter availability information!");
                    return;
                }
                provider.setAvailability(availabilityField.getText().trim());
                provider.manageSchedule();
                dataManager.saveProvider(provider);
                JOptionPane.showMessageDialog(this, "Availability updated!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        buttonPanel.add(updateBtn);

        JButton searchBtn = new JButton("Search Provider");
        searchBtn.addActionListener(e -> {
            try {
                String searchTerm = JOptionPane.showInputDialog(this, "Enter search term:");
                if (searchTerm != null) {
                    List<Provider> results = dataManager.searchProviders(searchTerm);
                    StringBuilder sb = new StringBuilder("Search Results:\n\n");
                    for (Provider p : results) {
                        sb.append(p.toString()).append("\n");
                    }
                    JOptionPane.showMessageDialog(this, sb.toString());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        buttonPanel.add(searchBtn);

        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JPanel createConsultationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Provider:"), gbc);
        gbc.gridx = 1;
        JComboBox<Provider> providerCombo = new JComboBox<>();
        try {
            List<Provider> providers = dataManager.getAllProviders();
            if (providers == null || providers.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No providers found in the system.");
            } else {
                for (Provider p : providers) {
                    if (p != null) {
                        providerCombo.addItem(p);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading providers: " + ex.getMessage());
            ex.printStackTrace();
        }
        providerCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Provider) {
                    setText(((Provider) value).getName() + " - " + ((Provider) value).getSpecialty());
                }
                return this;
            }
        });
        panel.add(providerCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Appointment:"), gbc);
        gbc.gridx = 1;
        JComboBox<Appointment> appointmentCombo = new JComboBox<>();
        
        // Method to refresh appointments based on selected provider
        Runnable refreshAppointments = () -> {
            appointmentCombo.removeAllItems();
            try {
                Provider selectedProvider = (Provider) providerCombo.getSelectedItem();
                if (selectedProvider != null) {
                    List<Appointment> appointments = dataManager.getAllAppointments();
                    for (Appointment apt : appointments) {
                        if (apt.getStatus().equals("Scheduled") && 
                            apt.getProvider() != null && 
                            apt.getProvider().getUserID().equals(selectedProvider.getUserID())) {
                            appointmentCombo.addItem(apt);
                        }
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading appointments: " + ex.getMessage());
            }
        };
        
        // Initial load
        refreshAppointments.run();
        
        // Refresh appointments when provider changes
        providerCombo.addActionListener(e -> refreshAppointments.run());
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

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton conductBtn = new JButton("Conduct Consultation");
        conductBtn.addActionListener(e -> {
            try {
                Provider provider = (Provider) providerCombo.getSelectedItem();
                Appointment appointment = (Appointment) appointmentCombo.getSelectedItem();
                
                if (provider == null) {
                    JOptionPane.showMessageDialog(this, "Please select a provider!");
                    return;
                }
                
                if (appointment == null) {
                    JOptionPane.showMessageDialog(this, "Please select an appointment!");
                    return;
                }
                
                // Validate that appointment belongs to selected provider
                if (appointment.getProvider() == null || 
                    !appointment.getProvider().getUserID().equals(provider.getUserID())) {
                    JOptionPane.showMessageDialog(this, "Selected appointment does not belong to the selected provider!");
                    return;
                }
                
                provider.conductConsultation(appointment);
                appointment.complete();
                dataManager.saveAppointment(appointment);
                dataManager.saveProvider(provider);
                JOptionPane.showMessageDialog(this, "Consultation completed!");

                // Refresh appointments list
                refreshAppointments.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        panel.add(conductBtn, gbc);

        return panel;
    }

    private JPanel createPrescriptionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Provider:"), gbc);
        gbc.gridx = 1;
        JComboBox<Provider> providerCombo = new JComboBox<>();
        try {
            List<Provider> providers = dataManager.getAllProviders();
            if (providers == null || providers.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No providers found in the system.");
            } else {
                for (Provider p : providers) {
                    if (p != null) {
                        providerCombo.addItem(p);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading providers: " + ex.getMessage());
            ex.printStackTrace();
        }
        providerCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Provider) {
                    setText(((Provider) value).getName() + " - " + ((Provider) value).getSpecialty());
                }
                return this;
            }
        });
        panel.add(providerCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1;
        JComboBox<Patient> patientCombo = new JComboBox<>();
        try {
            List<Patient> patients = dataManager.getAllPatients();
            for (Patient p : patients) {
                patientCombo.addItem(p);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading patients: " + ex.getMessage());
        }
        patientCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Patient) {
                    setText(((Patient) value).getName());
                }
                return this;
            }
        });
        panel.add(patientCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Medication:"), gbc);
        gbc.gridx = 1;
        medicationField = new JTextField(20);
        panel.add(medicationField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Dosage:"), gbc);
        gbc.gridx = 1;
        dosageField = new JTextField(20);
        panel.add(dosageField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton issueBtn = new JButton("Issue Prescription");
        issueBtn.addActionListener(e -> {
            try {
                Provider provider = (Provider) providerCombo.getSelectedItem();
                Patient patient = (Patient) patientCombo.getSelectedItem();
                
                if (provider == null) {
                    JOptionPane.showMessageDialog(this, "Please select a provider!");
                    return;
                }
                
                if (patient == null) {
                    JOptionPane.showMessageDialog(this, "Please select a patient!");
                    return;
                }
                
                String medication = medicationField.getText().trim();
                String dosage = dosageField.getText().trim();
                
                if (medication.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter medication name!");
                    return;
                }
                
                if (dosage.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter dosage information!");
                    return;
                }
                
                String prescriptionID = dataManager.generatePrescriptionID();
                Prescription prescription = provider.issuePrescription(
                    prescriptionID,
                    medication,
                    dosage,
                    patient.getUserID()
                );
                prescription.issue();
                

                String ehrID = dataManager.generateEHRID(patient.getUserID());
                EHR ehr = dataManager.loadEHR(ehrID);
                if (ehr == null) {
                    ehr = new EHR(ehrID, "Diagnosis pending", "");
                    ehr.setPatient(patient);
                }
                ehr.addPrescription(prescription);
                prescription.setEhr(ehr);
                ehr.updateRecord("Prescription issued: " + medication);
                
                dataManager.savePrescription(prescription);
                dataManager.saveEHR(ehr);
                dataManager.saveProvider(provider);
                JOptionPane.showMessageDialog(this, "Prescription issued successfully! ID: " + prescriptionID);
                
                // Clear fields after successful prescription
                medicationField.setText("");
                dosageField.setText("");
                patientCombo.setSelectedItem(null);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        panel.add(issueBtn, gbc);

        return panel;
    }

    private JPanel createAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Provider selection combo box
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Filter by Provider:"));
        JComboBox<Provider> providerFilterCombo = new JComboBox<>();
        providerFilterCombo.addItem(null); // Option to show all
        try {
            List<Provider> providers = dataManager.getAllProviders();
            if (providers != null && !providers.isEmpty()) {
                for (Provider p : providers) {
                    if (p != null) {
                        providerFilterCombo.addItem(p);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading providers: " + ex.getMessage());
            ex.printStackTrace();
        }
        providerFilterCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("-- All Providers --");
                } else if (value instanceof Provider) {
                    setText(((Provider) value).getName() + " - " + ((Provider) value).getSpecialty());
                }
                return this;
            }
        });
        topPanel.add(providerFilterCombo);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"Appointment ID", "Date/Time", "Status", "Patient", "Provider"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(appointmentTable);

        // Method to refresh appointments table
        Runnable refreshAppointmentsTable = () -> {
            try {
                Provider selectedProvider = (Provider) providerFilterCombo.getSelectedItem();
                List<Appointment> appointments = dataManager.getAllAppointments();
                tableModel.setRowCount(0);
                for (Appointment apt : appointments) {
                    // Filter by provider if one is selected
                    if (selectedProvider == null || 
                        (apt.getProvider() != null && apt.getProvider().getUserID().equals(selectedProvider.getUserID()))) {
                        tableModel.addRow(new Object[]{
                            apt.getAppointmentID(),
                            apt.getDateTime(),
                            apt.getStatus(),
                            apt.getPatient() != null ? apt.getPatient().getName() : "N/A",
                            apt.getProvider() != null ? apt.getProvider().getName() : "N/A"
                        });
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        };

        // Refresh when provider filter changes
        providerFilterCombo.addActionListener(e -> refreshAppointmentsTable.run());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshBtn = new JButton("Refresh Appointments");
        refreshBtn.addActionListener(e -> refreshAppointmentsTable.run());
        buttonPanel.add(refreshBtn);

        JButton deleteBtn = new JButton("Delete Selected Appointment");
        deleteBtn.addActionListener(e -> {
            int selectedRow = appointmentTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an appointment to delete!");
                return;
            }
            
            String appointmentID = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete appointment " + appointmentID + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    dataManager.deleteAppointment(appointmentID);
                    JOptionPane.showMessageDialog(this, "Appointment deleted successfully!");
                    refreshAppointmentsTable.run();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting appointment: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        buttonPanel.add(deleteBtn);

        // Initial load
        refreshAppointmentsTable.run();

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
}

