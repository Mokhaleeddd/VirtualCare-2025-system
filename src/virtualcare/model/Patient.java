package virtualcare.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Patient extends User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String contactInfo;
    private String medicalHistory;
    private List<Appointment> appointments;
    private List<Feedback> feedbacks;

    public Patient(String patientID, String name, String contactInfo, String medicalHistory) {
        super(patientID, name);
        this.contactInfo = contactInfo;
        this.medicalHistory = medicalHistory;
        this.appointments = new ArrayList<>();
        this.feedbacks = new ArrayList<>();
    }

    public void register() {
        System.out.println("Patient " + this.name + " has registered.");
    }

    public void updateProfile(String newContactInfo, String newMedicalHistory) {
        if (newContactInfo != null) {
            this.contactInfo = newContactInfo;
        }
        if (newMedicalHistory != null) {
            this.medicalHistory = newMedicalHistory;
        }
        System.out.println("Patient " + this.name + " profile updated.");
    }

    public Appointment bookAppointment(String appointmentID, String dateTime, Provider provider) {
        Appointment appointment = new Appointment(appointmentID, dateTime, "Scheduled");
        appointment.setProvider(provider);
        appointment.setPatient(this);
        appointments.add(appointment);
        if (provider != null) {
            provider.addAppointment(appointment);
        }
        System.out.println("Patient " + this.name + " booked appointment: " + appointmentID);
        return appointment;
    }

    public void viewEHR(EHR ehr) {
        if (ehr != null) {
            System.out.println("Patient " + this.name + " viewing EHR: " + ehr.getRecordID());
        }
    }

    public Feedback provideFeedback(String feedbackID, int rating, String comment) {
        Feedback feedback = new Feedback(feedbackID, rating, comment);
        feedbacks.add(feedback);
        System.out.println("Patient " + this.name + " provided feedback with rating: " + rating);
        return feedback;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public List<Appointment> getAppointments() {
        return new ArrayList<>(appointments);
    }

    public List<Feedback> getFeedbacks() {
        return new ArrayList<>(feedbacks);
    }

    @Override
    public String toString() {
        return "Patient{" +
                "patientID='" + userID + '\'' +
                ", name='" + name + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                ", medicalHistory='" + medicalHistory + '\'' +
                '}';
    }
}

