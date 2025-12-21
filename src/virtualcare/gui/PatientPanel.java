package virtualcare.gui;

import virtualcare.model.*;
import virtualcare.service.DataManager;
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
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, contactField;
    private JTextArea medicalHistoryField;
    private JComboBox<Integer> dayCombo, monthCombo, yearCombo, hourCombo, minuteCombo;
    private JComboBox<Integer> ratingField;
    private JTextArea commentField;

    public PatientPanel(DataManager dataManager) {
        this.dataManager = dataManager;
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());


        JLabel titleLabel = new JLabel("Patient Interface", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);


        JTabbedPane tabbedPane = new JTabbedPane();


        tabbedPane.addTab("Register/Update Profile", createRegistrationPanel());
        

        tabbedPane.addTab("Book Appointment", createAppointmentPanel());
        

        tabbedPane.addTab("View EHR", createEHRPanel());
        

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


        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Select Patient (for update):"), gbc);
        gbc.gridx = 1;
        JComboBox<Patient> patientCombo = new JComboBox<>();
        patientCombo.addItem(null); // Add null option for new registration
        try {
            List<Patient> patients = dataManager.getAllPatients();
            for (Patient p : patients) {
                patientCombo.addItem(p);
            }
        } catch (Exception ex) {

        }
        patientCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("-- New Patient --");
                } else if (value instanceof Patient) {
                    setText(((Patient) value).getName());
                }
                return this;
            }
        });
        patientCombo.addActionListener(e -> {
            Patient selected = (Patient) patientCombo.getSelectedItem();
            if (selected != null) {
                nameField.setText(selected.getName());
                contactField.setText(selected.getContactInfo());
                medicalHistoryField.setText(selected.getMedicalHistory());
            } else {
                nameField.setText("");
                contactField.setText("");
                medicalHistoryField.setText("");
            }
        });
        panel.add(patientCombo, gbc);


        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        panel.add(nameField, gbc);


        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Contact Info:"), gbc);
        gbc.gridx = 1;
        contactField = new JTextField(20);
        panel.add(contactField, gbc);


        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Medical History:"), gbc);
        gbc.gridx = 1;
        medicalHistoryField = new JTextArea(5, 20);
        medicalHistoryField.setLineWrap(true);
        medicalHistoryField.setWrapStyleWord(true);
        JScrollPane medicalHistoryScroll = new JScrollPane(medicalHistoryField);
        panel.add(medicalHistoryScroll, gbc);


        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton registerBtn = new JButton("Register New Patient");
        registerBtn.addActionListener(e -> {
            Patient selected = (Patient) patientCombo.getSelectedItem();
            if (selected == null) {
                registerPatient(patientCombo);
            } else {
                JOptionPane.showMessageDialog(this, "Please select '-- New Patient --' to register a new patient!");
            }
        });
        buttonPanel.add(registerBtn);

        JButton updateBtn = new JButton("Update Profile");
        updateBtn.addActionListener(e -> {
            Patient selected = (Patient) patientCombo.getSelectedItem();
            if (selected != null) {
                updatePatient(selected, patientCombo);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a patient to update!");
            }
        });
        buttonPanel.add(updateBtn);

        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JPanel createAppointmentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;


        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1;
        JComboBox<Patient> patientCombo = new JComboBox<>();
        try {
            List<Patient> patients = dataManager.getAllPatients();
            for (Patient p : patients) {
                patientCombo.addItem(p);
            }
        } catch (Exception ex) {

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
                Patient patient = (Patient) patientCombo.getSelectedItem();
                Provider provider = (Provider) providerCombo.getSelectedItem();
                
                Integer day = (Integer) dayCombo.getSelectedItem();
                Integer month = (Integer) monthCombo.getSelectedItem();
                Integer year = (Integer) yearCombo.getSelectedItem();
                Integer hour = (Integer) hourCombo.getSelectedItem();
                Integer minute = (Integer) minuteCombo.getSelectedItem();
                
                if (patient == null || provider == null || day == null || month == null || 
                    year == null || hour == null || minute == null) {
                    JOptionPane.showMessageDialog(this, "Please fill in all fields!");
                    return;
                }
                
                // Format date/time as dd/mm/yyyy HH:mm
                String dateTime = String.format("%02d/%02d/%04d %02d:%02d", day, month, year, hour, minute);
                
                String appointmentID = dataManager.generateAppointmentID();
                Appointment appointment = patient.bookAppointment(
                    appointmentID,
                    dateTime,
                    provider
                );
                dataManager.saveAppointment(appointment);
                dataManager.savePatient(patient);
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

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1;
        JComboBox<Patient> patientCombo = new JComboBox<>();
        try {
            List<Patient> patients = dataManager.getAllPatients();
            for (Patient p : patients) {
                patientCombo.addItem(p);
            }
        } catch (Exception ex) {

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

        JTextArea ehrDisplay = new JTextArea(15, 50);
        ehrDisplay.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(ehrDisplay);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(scrollPane, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton viewBtn = new JButton("View EHR");
        patientCombo.addActionListener(e -> {
            ehrDisplay.setText("");
        });
        
        viewBtn.addActionListener(e -> {
            try {
                Patient patient = (Patient) patientCombo.getSelectedItem();
                if (patient != null) {
                    EHR ehr = dataManager.loadEHR("EHR-" + patient.getUserID());
                    if (ehr != null) {
                        ehrDisplay.setText(ehr.getRecord());
                    } else {
                        ehrDisplay.setText("No EHR found for patient: " + patient.getName());
                    }
                } else {
                    ehrDisplay.setText("Please select a patient!");
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


        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1;
        JComboBox<Patient> patientCombo = new JComboBox<>();
        try {
            List<Patient> patients = dataManager.getAllPatients();
            for (Patient p : patients) {
                patientCombo.addItem(p);
            }
        } catch (Exception ex) {

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
                Patient patient = (Patient) patientCombo.getSelectedItem();
                if (patient == null) {
                    JOptionPane.showMessageDialog(this, "Please select a patient!");
                    return;
                }
                
                Integer rating = (Integer) ratingField.getSelectedItem();
                if (rating == null) {
                    JOptionPane.showMessageDialog(this, "Please select a rating!");
                    return;
                }
                
                String feedbackID = dataManager.generateFeedbackID();
                Feedback feedback = patient.provideFeedback(
                    feedbackID,
                    rating,
                    commentField.getText()
                );
                dataManager.saveFeedback(feedback);
                dataManager.savePatient(patient);
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
            try {
                List<Appointment> appointments = dataManager.getAllAppointments();
                tableModel.setRowCount(0);
                for (Appointment apt : appointments) {
                    tableModel.addRow(new Object[]{
                        apt.getAppointmentID(),
                        apt.getDateTime(),
                        apt.getStatus(),
                        apt.getProvider() != null ? apt.getProvider().getName() : "N/A"
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
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
                    dataManager.deleteAppointment(appointmentID);
                    JOptionPane.showMessageDialog(this, "Appointment deleted successfully!");
                    
                    // Refresh the table
                    List<Appointment> appointments = dataManager.getAllAppointments();
                    tableModel.setRowCount(0);
                    for (Appointment apt : appointments) {
                        tableModel.addRow(new Object[]{
                            apt.getAppointmentID(),
                            apt.getDateTime(),
                            apt.getStatus(),
                            apt.getProvider() != null ? apt.getProvider().getName() : "N/A"
                        });
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

    private void registerPatient(JComboBox<Patient> patientCombo) {
        try {
            if (nameField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a name!");
                return;
            }
            String patientID = dataManager.generatePatientID();
            Patient patient = new Patient(
                patientID,
                nameField.getText(),
                contactField.getText(),
                medicalHistoryField.getText()
            );
            patient.register();
            dataManager.savePatient(patient);
            JOptionPane.showMessageDialog(this, "Patient registered successfully! ID: " + patientID);
            nameField.setText("");
            contactField.setText("");
            medicalHistoryField.setText("");

            patientCombo.removeAllItems();
            patientCombo.addItem(null);
            List<Patient> patients = dataManager.getAllPatients();
            for (Patient p : patients) {
                patientCombo.addItem(p);
            }
            patientCombo.setSelectedItem(null);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updatePatient(Patient patient, JComboBox<Patient> patientCombo) {
        try {
            if (patient != null) {
                patient.setName(nameField.getText());
                patient.updateProfile(contactField.getText(), medicalHistoryField.getText());
                dataManager.savePatient(patient);
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");

                patientCombo.removeAllItems();
                patientCombo.addItem(null);
                List<Patient> patients = dataManager.getAllPatients();
                for (Patient p : patients) {
                    patientCombo.addItem(p);
                }
                patientCombo.setSelectedItem(patient);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}

