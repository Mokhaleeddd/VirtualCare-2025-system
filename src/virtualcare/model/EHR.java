package virtualcare.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EHR implements Serializable {
    private static final long serialVersionUID = 1L;
    private String recordID;
    private String diagnosis;
    private String treatmentPlan;
    private List<Prescription> prescriptions;
    private Patient patient;

    public EHR(String recordID, String diagnosis, String treatmentPlan) {
        this.recordID = recordID;
        this.diagnosis = diagnosis;
        this.treatmentPlan = treatmentPlan;
        this.prescriptions = new ArrayList<>();
    }

    public void updateRecord(String updateInfo) {
        if (treatmentPlan == null || treatmentPlan.isEmpty()) {
            treatmentPlan = updateInfo;
        } else {
            treatmentPlan += "\n" + updateInfo;
        }
        System.out.println("EHR " + recordID + " updated.");
    }

    public String getRecord() {
        StringBuilder record = new StringBuilder();
        record.append("Record ID: ").append(recordID).append("\n");
        record.append("Diagnosis: ").append(diagnosis).append("\n");
        record.append("Treatment Plan: ").append(treatmentPlan).append("\n");
        record.append("Prescriptions: ").append(prescriptions.size()).append("\n");
        return record.toString();
    }

    public void addPrescription(Prescription prescription) {
        if (prescription != null && !prescriptions.contains(prescription)) {
            prescriptions.add(prescription);
        }
    }

    public String getRecordID() {
        return recordID;
    }

    public void setRecordID(String recordID) {
        this.recordID = recordID;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(String treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }

    public List<Prescription> getPrescriptions() {
        return new ArrayList<>(prescriptions);
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @Override
    public String toString() {
        return "EHR{" +
                "recordID='" + recordID + '\'' +
                ", diagnosis='" + diagnosis + '\'' +
                ", treatmentPlan='" + treatmentPlan + '\'' +
                ", prescriptions=" + prescriptions.size() +
                '}';
    }
}

