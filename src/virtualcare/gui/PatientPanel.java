package virtualcare.gui;

import virtualcare.model.*;
import virtualcare.service.DataManager;
import virtualcare.service.AuthenticationService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class PatientPanel extends JPanel {
    private DataManager dataManager;
    private AuthenticationService authService;
    private Patient currentPatient;
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, contactField;
    private JTextArea medicalHistoryField;
    private JComboBox<Integer> dayCombo, monthCombo, yearCombo, hourCombo, minuteCombo;
    private JComboBox<Integer> ratingField;
    private JTextArea commentField;

    public PatientPanel(DataManager dataManager, AuthenticationService authService) {
        this.dataManager = dataManager;
        this.authService = authService;
        loadCurrentPatient();
        initializePanel();
    }
    
    /**
     * Refreshes the panel - reloads patient data and reinitializes UI
     * Call this when switching to this panel after login
     */
    public void refreshPanel() {
        removeAll();
        loadCurrentPatient();
        initializePanel();
        revalidate();
        repaint();
    }
    
    /**
     * Loads the currently logged-in patient
     */
    private void loadCurrentPatient() {
        User currentUser = authService.getCurrentUser();
        if (currentUser instanceof Patient) {
            this.currentPatient = (Patient) currentUser;
        } else {
            this.currentPatient = null;
        }
    }
    
    /**
     * Checks if current patient is authenticated
     */
    private boolean isAuthenticated() {
        // Reload current patient to ensure we have the latest authentication state
        loadCurrentPatient();
        return currentPatient != null;
    }

    private void initializePanel() {
        setLayout(new BorderLayout());

        // Reload current patient in case panel was created before login
        loadCurrentPatient();

        // Check authentication
        if (!isAuthenticated()) {
            JLabel errorLabel = new JLabel("Please login as a patient to access this panel.", JLabel.CENTER);
            errorLabel.setFont(new Font("Arial", Font.BOLD, 16));
            errorLabel.setForeground(Color.RED);
            add(errorLabel, BorderLayout.CENTER);
            return;
        }

        // Welcome label with patient name
        String welcomeText = "Welcome, " + currentPatient.getName() + " (ID: " + currentPatient.getUserID() + ")";
        JLabel titleLabel = new JLabel(welcomeText, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("My Profile", createRegistrationPanel());
        tabbedPane.addTab("Book Appointment", createAppointmentPanel());
        tabbedPane.addTab("View My EHR", createEHRPanel());
        tabbedPane.addTab("Provide Feedback", createFeedbackPanel());
        tabbedPane.addTab("My Appointments", createAppointmentsListPanel());

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

    private JPanel createRegistrationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Display current patient info (read-only)
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Patient ID:"), gbc);
        gbc.gridx = 1;
        JLabel patientIDLabel = new JLabel(currentPatient.getUserID());
        patientIDLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(patientIDLabel, gbc);


        // Pre-populate with current patient data
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        nameField.setText(currentPatient.getName());
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Contact Info:"), gbc);
        gbc.gridx = 1;
        contactField = new JTextField(20);
        contactField.setText(currentPatient.getContactInfo() != null ? currentPatient.getContactInfo() : "");
        panel.add(contactField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Medical History:"), gbc);
        gbc.gridx = 1;
        medicalHistoryField = new JTextArea(5, 20);
        medicalHistoryField.setLineWrap(true);
        medicalHistoryField.setWrapStyleWord(true);
        medicalHistoryField.setText(currentPatient.getMedicalHistory() != null ? currentPatient.getMedicalHistory() : "");
        JScrollPane medicalHistoryScroll = new JScrollPane(medicalHistoryField);
        panel.add(medicalHistoryScroll, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton updateBtn = new JButton("Update My Profile");
        updateBtn.addActionListener(e -> {
            updatePatient(currentPatient);
        });
        panel.add(updateBtn, gbc);

        return panel;
    }

    private JPanel createAppointmentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Display current patient (read-only) for appointment booking
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1;
        JLabel appointmentPatientLabel = new JLabel(currentPatient.getName() + " (ID: " + currentPatient.getUserID() + ")");
        appointmentPatientLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(appointmentPatientLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Date/Time:"), gbc);
        gbc.gridx = 1;
        JPanel dateTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        
        // Day dropdown (1-31)
        dayCombo = new JComboBox<Integer>();
        dayCombo.addItem(null);
        for (int i = 1; i <= 31; i++) {
            dayCombo.addItem(i);
        }
        dayCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("DD");
                } else {
                    setText(String.format("%02d", value));
                }
                return this;
            }
        });
        dateTimePanel.add(dayCombo);
        dateTimePanel.add(new JLabel("/"));
        
        // Month dropdown (1-12)
        monthCombo = new JComboBox<Integer>();
        monthCombo.addItem(null);
        for (int i = 1; i <= 12; i++) {
            monthCombo.addItem(i);
        }
        monthCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("MM");
                } else {
                    setText(String.format("%02d", value));
                }
                return this;
            }
        });
        dateTimePanel.add(monthCombo);
        dateTimePanel.add(new JLabel("/"));
        
        // Year dropdown (current year to 5 years ahead)
        yearCombo = new JComboBox<Integer>();
        yearCombo.addItem(null);
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear; i <= currentYear + 5; i++) {
            yearCombo.addItem(i);
        }
        yearCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("YYYY");
                } else {
                    setText(value.toString());
                }
                return this;
            }
        });
        dateTimePanel.add(yearCombo);
        dateTimePanel.add(new JLabel(" "));
        
        // Hour dropdown (0-23)
        hourCombo = new JComboBox<Integer>();
        hourCombo.addItem(null);
        for (int i = 0; i <= 23; i++) {
            hourCombo.addItem(i);
        }
        hourCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("HH");
                } else {
                    setText(String.format("%02d", value));
                }
                return this;
            }
        });
        dateTimePanel.add(hourCombo);
        dateTimePanel.add(new JLabel(":"));
        
        // Minute dropdown (0-59, in 15-minute intervals)
        minuteCombo = new JComboBox<Integer>();
        minuteCombo.addItem(null);
        for (int i = 0; i <= 59; i += 15) {
            minuteCombo.addItem(i);
        }
        minuteCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("MM");
                } else {
                    setText(String.format("%02d", value));
                }
                return this;
            }
        });
        dateTimePanel.add(minuteCombo);
        
        panel.add(dateTimePanel, gbc);


        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Provider:"), gbc);
        gbc.gridx = 1;
        JComboBox<Provider> providerCombo = new JComboBox<>();
        try {
            List<Provider> providers = dataManager.getAllProviders();
            for (Provider p : providers) {
                providerCombo.addItem(p);
            }
        } catch (Exception ex) {

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


        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton bookBtn = new JButton("Book Appointment");
        bookBtn.addActionListener(e -> {
            try {
                Provider provider = (Provider) providerCombo.getSelectedItem();
                
                Integer day = (Integer) dayCombo.getSelectedItem();
                Integer month = (Integer) monthCombo.getSelectedItem();
                Integer year = (Integer) yearCombo.getSelectedItem();
                Integer hour = (Integer) hourCombo.getSelectedItem();
                Integer minute = (Integer) minuteCombo.getSelectedItem();
                
                if (provider == null || day == null || month == null || 
                    year == null || hour == null || minute == null) {
                    JOptionPane.showMessageDialog(this, "Please fill in all fields!");
                    return;
                }
                
                // Format date/time as dd/mm/yyyy HH:mm
                String dateTime = String.format("%02d/%02d/%04d %02d:%02d", day, month, year, hour, minute);
                
                String appointmentID = dataManager.generateAppointmentID();
                Appointment appointment = currentPatient.bookAppointment(
                    appointmentID,
                    dateTime,
                    provider
                );
                dataManager.saveAppointment(appointment);
                dataManager.savePatient(currentPatient);
                JOptionPane.showMessageDialog(this, "Appointment booked successfully! ID: " + appointmentID);
                
                // Reset dropdowns
                dayCombo.setSelectedItem(null);
                monthCombo.setSelectedItem(null);
                yearCombo.setSelectedItem(null);
                hourCombo.setSelectedItem(null);
                minuteCombo.setSelectedItem(null);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        panel.add(bookBtn, gbc);

        return panel;
    }

    private JPanel createEHRPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Display current patient (read-only)
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1;
        JLabel ehrPatientLabel = new JLabel(currentPatient.getName() + " (ID: " + currentPatient.getUserID() + ")");
        ehrPatientLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(ehrPatientLabel, gbc);

        JTextArea ehrDisplay = new JTextArea(15, 50);
        ehrDisplay.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(ehrDisplay);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(scrollPane, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton viewBtn = new JButton("View My EHR");
        
        // Auto-load EHR on panel display
        try {
            EHR ehr = dataManager.loadEHR("EHR-" + currentPatient.getUserID());
            if (ehr != null) {
                ehrDisplay.setText(ehr.getRecord());
            } else {
                ehrDisplay.setText("No EHR found for you. Your EHR will be created when a provider issues a prescription.");
            }
        } catch (Exception ex) {
            ehrDisplay.setText("Error loading EHR: " + ex.getMessage());
        }
        
        viewBtn.addActionListener(e -> {
            try {
                EHR ehr = dataManager.loadEHR("EHR-" + currentPatient.getUserID());
                if (ehr != null) {
                    ehrDisplay.setText(ehr.getRecord());
                } else {
                    ehrDisplay.setText("No EHR found for you. Your EHR will be created when a provider issues a prescription.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        panel.add(viewBtn, gbc);

        return panel;
    }

    private JPanel createFeedbackPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;


        // Display current patient (read-only)
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1;
        JLabel feedbackPatientLabel = new JLabel(currentPatient.getName() + " (ID: " + currentPatient.getUserID() + ")");
        feedbackPatientLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(feedbackPatientLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Rating (1-5):"), gbc);
        gbc.gridx = 1;
        ratingField = new JComboBox<Integer>();
        ratingField.addItem(null); // Add null option for "select rating"
        for (int i = 1; i <= 5; i++) {
            ratingField.addItem(i);
        }
        ratingField.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("-- Select Rating --");
                } else {
                    setText(value.toString());
                }
                return this;
            }
        });
        panel.add(ratingField, gbc);


        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Comment:"), gbc);
        gbc.gridx = 1;
        commentField = new JTextArea(5, 20);
        commentField.setLineWrap(true);
        commentField.setWrapStyleWord(true);
        JScrollPane commentScroll = new JScrollPane(commentField);
        panel.add(commentScroll, gbc);


        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton submitBtn = new JButton("Submit Feedback");
        submitBtn.addActionListener(e -> {
            try {
                Integer rating = (Integer) ratingField.getSelectedItem();
                if (rating == null) {
                    JOptionPane.showMessageDialog(this, "Please select a rating!");
                    return;
                }
                
                String feedbackID = dataManager.generateFeedbackID();
                Feedback feedback = currentPatient.provideFeedback(
                    feedbackID,
                    rating,
                    commentField.getText()
                );
                dataManager.saveFeedback(feedback);
                dataManager.savePatient(currentPatient);
                JOptionPane.showMessageDialog(this, "Feedback submitted successfully!");
                ratingField.setSelectedItem(null);
                commentField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        panel.add(submitBtn, gbc);

        return panel;
    }

    private JPanel createAppointmentsListPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"Appointment ID", "Date/Time", "Status", "Provider"};
        tableModel = new DefaultTableModel(columnNames, 0);
        patientTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(patientTable);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshBtn = new JButton("Refresh Appointments");
        refreshBtn.addActionListener(e -> {
            refreshAppointmentsTable();
        });
        
        // Initial load
        refreshAppointmentsTable();
        buttonPanel.add(refreshBtn);

        JButton deleteBtn = new JButton("Delete Selected Appointment");
        deleteBtn.addActionListener(e -> {
            int selectedRow = patientTable.getSelectedRow();
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
                    // Verify the appointment belongs to current patient
                    Appointment apt = dataManager.loadAppointment(appointmentID);
                    if (apt != null && apt.getPatient() != null && 
                        apt.getPatient().getUserID().equals(currentPatient.getUserID())) {
                        dataManager.deleteAppointment(appointmentID);
                        JOptionPane.showMessageDialog(this, "Appointment deleted successfully!");
                        refreshAppointmentsTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "Unauthorized: This appointment does not belong to you.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting appointment: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        buttonPanel.add(deleteBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }


    /**
     * Updates the current patient's profile
     */
    private void updatePatient(Patient patient) {
        try {
            if (patient == null || !patient.getUserID().equals(currentPatient.getUserID())) {
                JOptionPane.showMessageDialog(this, "Unauthorized: You can only update your own profile.");
                return;
            }
            
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a name!");
                return;
            }
            
            currentPatient.setName(nameField.getText().trim());
            currentPatient.updateProfile(contactField.getText(), medicalHistoryField.getText());
            dataManager.savePatient(currentPatient);
            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            
            // Reload patient data to refresh UI
            loadCurrentPatient();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    /**
     * Refreshes the appointments table with only current patient's appointments
     */
    private void refreshAppointmentsTable() {
        try {
            List<Appointment> appointments = dataManager.getAllAppointments();
            tableModel.setRowCount(0);
            for (Appointment apt : appointments) {
                // Only show appointments belonging to current patient
                if (apt.getPatient() != null && apt.getPatient().getUserID().equals(currentPatient.getUserID())) {
                    tableModel.addRow(new Object[]{
                        apt.getAppointmentID(),
                        apt.getDateTime(),
                        apt.getStatus(),
                        apt.getProvider() != null ? apt.getProvider().getName() : "N/A"
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}

