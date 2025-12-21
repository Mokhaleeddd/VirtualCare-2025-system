package virtualcare.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment implements Serializable {
    private static final long serialVersionUID = 1L;
    private String appointmentID;
    private String dateTime;
    private String status;
    private Provider provider;
    private Patient patient;
    private EHR ehr;
    private Bill bill;

    public Appointment(String appointmentID, String dateTime, String status) {
        this.appointmentID = appointmentID;
        this.dateTime = dateTime;
        this.status = status;
    }

    public void schedule() {
        this.status = "Scheduled";
        System.out.println("Appointment " + appointmentID + " scheduled for " + dateTime);
    }

    public void cancel() {
        this.status = "Cancelled";
        System.out.println("Appointment " + appointmentID + " cancelled.");
    }

    public void complete() {
        this.status = "Completed";
        System.out.println("Appointment " + appointmentID + " completed.");
        

        if (ehr != null) {
            ehr.updateRecord("Appointment completed on " + dateTime);
        }
        

        if (bill == null && patient != null) {
            bill = new Bill("BILL-" + appointmentID, 150.0, "Pending", "Credit Card");
            bill.generate();
        }
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public EHR getEhr() {
        return ehr;
    }

    public void setEhr(EHR ehr) {
        this.ehr = ehr;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentID='" + appointmentID + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", status='" + status + '\'' +
                ", provider=" + (provider != null ? provider.getName() : "None") +
                ", patient=" + (patient != null ? patient.getName() : "None") +
                '}';
    }
}

