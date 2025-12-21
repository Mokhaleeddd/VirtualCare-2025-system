package virtualcare.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Provider extends User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String specialty;
    private String availability;
    private List<Appointment> appointments;
    private Admin managedBy;

    public Provider(String providerID, String name, String specialty, String availability) {
        super(providerID, name);
        this.specialty = specialty;
        this.availability = availability;
        this.appointments = new ArrayList<>();
    }

    public void manageSchedule() {
        System.out.println("Provider " + this.name + " is managing schedule.");
    }

    public void conductConsultation(Appointment appointment) {
        if (appointment != null && appointments.contains(appointment)) {
            System.out.println("Provider " + this.name + " is conducting consultation for appointment " + appointment.getAppointmentID());
        }
    }

    public Prescription issuePrescription(String prescriptionID, String medication, String dosage, String patientID) {
        Prescription prescription = new Prescription(prescriptionID, medication, dosage);
        System.out.println("Provider " + this.name + " issued prescription: " + medication);
        return prescription;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public List<Appointment> getAppointments() {
        return new ArrayList<>(appointments);
    }

    public void addAppointment(Appointment appointment) {
        if (appointment != null && !appointments.contains(appointment)) {
            appointments.add(appointment);
        }
    }

    public Admin getManagedBy() {
        return managedBy;
    }

    public void setManagedBy(Admin managedBy) {
        this.managedBy = managedBy;
    }

    @Override
    public String toString() {
        return "Provider{" +
                "providerID='" + userID + '\'' +
                ", name='" + name + '\'' +
                ", specialty='" + specialty + '\'' +
                ", availability='" + availability + '\'' +
                '}';
    }
}

