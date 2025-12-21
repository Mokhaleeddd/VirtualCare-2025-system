package virtualcare.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Prescription implements Serializable {
    private static final long serialVersionUID = 1L;
    private String prescriptionID;
    private String medication;
    private String dosage;
    private String issuedDate;
    private EHR ehr;

    public Prescription(String prescriptionID, String medication, String dosage) {
        this.prescriptionID = prescriptionID;
        this.medication = medication;
        this.dosage = dosage;
        this.issuedDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public void issue() {
        System.out.println("Prescription " + prescriptionID + " issued: " + medication + " - " + dosage);
    }

    public void renew() {
        System.out.println("Prescription " + prescriptionID + " renewed.");
    }

    public String getPrescriptionID() {
        return prescriptionID;
    }

    public void setPrescriptionID(String prescriptionID) {
        this.prescriptionID = prescriptionID;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
    }

    public EHR getEhr() {
        return ehr;
    }

    public void setEhr(EHR ehr) {
        this.ehr = ehr;
    }

    @Override
    public String toString() {
        return "Prescription{" +
                "prescriptionID='" + prescriptionID + '\'' +
                ", medication='" + medication + '\'' +
                ", dosage='" + dosage + '\'' +
                ", issuedDate='" + issuedDate + '\'' +
                '}';
    }
}

