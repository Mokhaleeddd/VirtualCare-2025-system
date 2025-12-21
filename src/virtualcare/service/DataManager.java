package virtualcare.service;

import virtualcare.model.*;
import java.io.*;
import java.util.*;

public class DataManager {
    private static final String DATA_DIR = "data/";
    private static final String PATIENTS_FILE = DATA_DIR + "patients.dat";
    private static final String PROVIDERS_FILE = DATA_DIR + "providers.dat";
    private static final String ADMINS_FILE = DATA_DIR + "admins.dat";
    private static final String APPOINTMENTS_FILE = DATA_DIR + "appointments.dat";
    private static final String EHRS_FILE = DATA_DIR + "ehrs.dat";
    private static final String BILLS_FILE = DATA_DIR + "bills.dat";
    private static final String FEEDBACKS_FILE = DATA_DIR + "feedbacks.dat";
    private static final String PRESCRIPTIONS_FILE = DATA_DIR + "prescriptions.dat";

    private Map<String, Patient> patients;
    private Map<String, Provider> providers;
    private Map<String, Admin> admins;
    private Map<String, Appointment> appointments;
    private Map<String, EHR> ehrs;
    private Map<String, Bill> bills;
    private Map<String, Feedback> feedbacks;
    private Map<String, Prescription> prescriptions;
    
    private static final String COUNTERS_FILE = DATA_DIR + "counters.dat";
    private int patientCounter = 1;
    private int providerCounter = 1;
    private int adminCounter = 1;
    private int appointmentCounter = 1;
    private int ehrCounter = 1;
    private int billCounter = 1;
    private int feedbackCounter = 1;
    private int prescriptionCounter = 1;

    public DataManager() {
        patients = new HashMap<>();
        providers = new HashMap<>();
        admins = new HashMap<>();
        appointments = new HashMap<>();
        ehrs = new HashMap<>();
        bills = new HashMap<>();
        feedbacks = new HashMap<>();
        prescriptions = new HashMap<>();
        

        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        

        loadCounters();
    }
    

    private void loadCounters() {
        File file = new File(COUNTERS_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(COUNTERS_FILE))) {
                Map<String, Integer> counters = (Map<String, Integer>) ois.readObject();
                patientCounter = counters.getOrDefault("patient", 1);
                providerCounter = counters.getOrDefault("provider", 1);
                adminCounter = counters.getOrDefault("admin", 1);
                appointmentCounter = counters.getOrDefault("appointment", 1);
                ehrCounter = counters.getOrDefault("ehr", 1);
                billCounter = counters.getOrDefault("bill", 1);
                feedbackCounter = counters.getOrDefault("feedback", 1);
                prescriptionCounter = counters.getOrDefault("prescription", 1);
            } catch (Exception e) {

            }
        }

        updateCountersFromData();
    }
    
    private void saveCounters() {
        try {
            Map<String, Integer> counters = new HashMap<>();
            counters.put("patient", patientCounter);
            counters.put("provider", providerCounter);
            counters.put("admin", adminCounter);
            counters.put("appointment", appointmentCounter);
            counters.put("ehr", ehrCounter);
            counters.put("bill", billCounter);
            counters.put("feedback", feedbackCounter);
            counters.put("prescription", prescriptionCounter);
            
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(COUNTERS_FILE))) {
                oos.writeObject(counters);
            }
        } catch (IOException e) {

        }
    }
    
    private void updateCountersFromData() {
        try {
            loadAllPatients();
            loadAllProviders();
            loadAllAdmins();
            loadAllAppointments();
            loadAllEHRs();
            loadAllBills();
            loadAllFeedbacks();
            loadAllPrescriptions();
            

            for (Patient p : patients.values()) {
                try {
                    int id = Integer.parseInt(p.getUserID().replace("PAT", ""));
                    if (id >= patientCounter) patientCounter = id + 1;
                } catch (NumberFormatException e) {}
            }
            for (Provider p : providers.values()) {
                try {
                    int id = Integer.parseInt(p.getUserID().replace("PROV", ""));
                    if (id >= providerCounter) providerCounter = id + 1;
                } catch (NumberFormatException e) {}
            }
            for (Admin a : admins.values()) {
                try {
                    int id = Integer.parseInt(a.getUserID().replace("ADM", ""));
                    if (id >= adminCounter) adminCounter = id + 1;
                } catch (NumberFormatException e) {}
            }
            for (Appointment a : appointments.values()) {
                try {
                    int id = Integer.parseInt(a.getAppointmentID().replace("APT", ""));
                    if (id >= appointmentCounter) appointmentCounter = id + 1;
                } catch (NumberFormatException e) {}
            }
            for (EHR e : ehrs.values()) {
                try {
                    int id = Integer.parseInt(e.getRecordID().replace("EHR-", "").replace("PAT", ""));
                    if (id >= ehrCounter) ehrCounter = id + 1;
                } catch (NumberFormatException e1) {}
            }
            for (Bill b : bills.values()) {
                try {
                    int id = Integer.parseInt(b.getBillID().replace("BILL-", "").replace("APT", ""));
                    if (id >= billCounter) billCounter = id + 1;
                } catch (NumberFormatException e) {}
            }
            for (Feedback f : feedbacks.values()) {
                try {
                    int id = Integer.parseInt(f.getFeedbackID().replace("FB", ""));
                    if (id >= feedbackCounter) feedbackCounter = id + 1;
                } catch (NumberFormatException e) {}
            }
            for (Prescription p : prescriptions.values()) {
                try {
                    int id = Integer.parseInt(p.getPrescriptionID().replace("PRES", ""));
                    if (id >= prescriptionCounter) prescriptionCounter = id + 1;
                } catch (NumberFormatException e) {}
            }
        } catch (Exception e) {

        }
    }
    
    public String generatePatientID() {
        String id = "PAT" + patientCounter++;
        saveCounters();
        return id;
    }
    
    public String generateProviderID() {
        String id = "PROV" + providerCounter++;
        saveCounters();
        return id;
    }
    
    public String generateAdminID() {
        String id = "ADM" + adminCounter++;
        saveCounters();
        return id;
    }
    
    public String generateAppointmentID() {
        String id = "APT" + appointmentCounter++;
        saveCounters();
        return id;
    }
    
    public String generateEHRID(String patientID) {
        return "EHR-" + patientID;
    }
    
    public String generateBillID(String appointmentID) {
        return "BILL-" + appointmentID;
    }
    
    public String generateFeedbackID() {
        String id = "FB" + feedbackCounter++;
        saveCounters();
        return id;
    }
    
    public String generatePrescriptionID() {
        String id = "PRES" + prescriptionCounter++;
        saveCounters();
        return id;
    }

    public void savePatient(Patient patient) throws IOException {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null");
        }
        if (patient.getUserID() == null || patient.getUserID().isEmpty()) {
            patient.setUserID(generatePatientID());
        }
        patients.put(patient.getUserID(), patient);
        saveAllPatients();
    }
    
    public Patient findPatientByName(String name) throws IOException, ClassNotFoundException {
        loadAllPatients();
        for (Patient p : patients.values()) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }
    
    public Provider findProviderByName(String name) throws IOException, ClassNotFoundException {
        loadAllProviders();
        for (Provider p : providers.values()) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }
    
    public Admin findAdminByName(String name) throws IOException, ClassNotFoundException {
        loadAllAdmins();
        for (Admin a : admins.values()) {
            if (a.getName().equalsIgnoreCase(name)) {
                return a;
            }
        }
        return null;
    }

    public Patient loadPatient(String patientID) throws IOException, ClassNotFoundException {
        loadAllPatients();
        return patients.get(patientID);
    }

    public List<Patient> searchPatients(String searchTerm) throws IOException, ClassNotFoundException {
        loadAllPatients();
        List<Patient> results = new ArrayList<>();
        String lowerSearch = searchTerm.toLowerCase();
        for (Patient patient : patients.values()) {
            if (patient.getUserID().toLowerCase().contains(lowerSearch) ||
                patient.getName().toLowerCase().contains(lowerSearch) ||
                patient.getContactInfo().toLowerCase().contains(lowerSearch)) {
                results.add(patient);
            }
        }
        return results;
    }

    private void saveAllPatients() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PATIENTS_FILE))) {
            oos.writeObject(new ArrayList<>(patients.values()));
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAllPatients() throws IOException, ClassNotFoundException {
        File file = new File(PATIENTS_FILE);
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PATIENTS_FILE))) {
            List<Patient> loadedPatients = (List<Patient>) ois.readObject();
            patients.clear();
            for (Patient patient : loadedPatients) {
                patients.put(patient.getUserID(), patient);
            }
        }
    }


    public void saveProvider(Provider provider) throws IOException {
        if (provider == null) {
            throw new IllegalArgumentException("Provider cannot be null");
        }
        providers.put(provider.getUserID(), provider);
        saveAllProviders();
    }

    public Provider loadProvider(String providerID) throws IOException, ClassNotFoundException {
        loadAllProviders();
        return providers.get(providerID);
    }

    public List<Provider> searchProviders(String searchTerm) throws IOException, ClassNotFoundException {
        loadAllProviders();
        List<Provider> results = new ArrayList<>();
        String lowerSearch = searchTerm.toLowerCase();
        for (Provider provider : providers.values()) {
            if (provider.getUserID().toLowerCase().contains(lowerSearch) ||
                provider.getName().toLowerCase().contains(lowerSearch) ||
                provider.getSpecialty().toLowerCase().contains(lowerSearch)) {
                results.add(provider);
            }
        }
        return results;
    }

    public void deleteProvider(String providerID) throws IOException, ClassNotFoundException {
        loadAllProviders();
        if (providers.containsKey(providerID)) {
            providers.remove(providerID);
            saveAllProviders();
        } else {
            throw new IllegalArgumentException("Provider with ID " + providerID + " not found");
        }
    }

    private void saveAllProviders() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PROVIDERS_FILE))) {
            oos.writeObject(new ArrayList<>(providers.values()));
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAllProviders() throws IOException, ClassNotFoundException {
        File file = new File(PROVIDERS_FILE);
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PROVIDERS_FILE))) {
            List<Provider> loadedProviders = (List<Provider>) ois.readObject();
            providers.clear();
            for (Provider provider : loadedProviders) {
                providers.put(provider.getUserID(), provider);
            }
        }
    }


    public void saveAdmin(Admin admin) throws IOException {
        if (admin == null) {
            throw new IllegalArgumentException("Admin cannot be null");
        }
        admins.put(admin.getUserID(), admin);
        saveAllAdmins();
    }

    public Admin loadAdmin(String adminID) throws IOException, ClassNotFoundException {
        loadAllAdmins();
        return admins.get(adminID);
    }

    private void saveAllAdmins() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ADMINS_FILE))) {
            oos.writeObject(new ArrayList<>(admins.values()));
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAllAdmins() throws IOException, ClassNotFoundException {
        File file = new File(ADMINS_FILE);
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ADMINS_FILE))) {
            List<Admin> loadedAdmins = (List<Admin>) ois.readObject();
            admins.clear();
            for (Admin admin : loadedAdmins) {
                admins.put(admin.getUserID(), admin);
            }
        }
    }


    public void saveAppointment(Appointment appointment) throws IOException {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null");
        }
        appointments.put(appointment.getAppointmentID(), appointment);
        saveAllAppointments();
    }

    public Appointment loadAppointment(String appointmentID) throws IOException, ClassNotFoundException {
        loadAllAppointments();
        return appointments.get(appointmentID);
    }

    public List<Appointment> searchAppointments(String searchTerm) throws IOException, ClassNotFoundException {
        loadAllAppointments();
        List<Appointment> results = new ArrayList<>();
        String lowerSearch = searchTerm.toLowerCase();
        for (Appointment appointment : appointments.values()) {
            if (appointment.getAppointmentID().toLowerCase().contains(lowerSearch) ||
                appointment.getStatus().toLowerCase().contains(lowerSearch) ||
                appointment.getDateTime().toLowerCase().contains(lowerSearch)) {
                results.add(appointment);
            }
        }
        return results;
    }

    public void deleteAppointment(String appointmentID) throws IOException, ClassNotFoundException {
        loadAllAppointments();
        if (appointments.containsKey(appointmentID)) {
            appointments.remove(appointmentID);
            saveAllAppointments();
        } else {
            throw new IllegalArgumentException("Appointment with ID " + appointmentID + " not found");
        }
    }

    private void saveAllAppointments() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(APPOINTMENTS_FILE))) {
            oos.writeObject(new ArrayList<>(appointments.values()));
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAllAppointments() throws IOException, ClassNotFoundException {
        File file = new File(APPOINTMENTS_FILE);
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(APPOINTMENTS_FILE))) {
            List<Appointment> loadedAppointments = (List<Appointment>) ois.readObject();
            appointments.clear();
            for (Appointment appointment : loadedAppointments) {
                appointments.put(appointment.getAppointmentID(), appointment);
            }
        }
    }


    public void saveEHR(EHR ehr) throws IOException {
        if (ehr == null) {
            throw new IllegalArgumentException("EHR cannot be null");
        }
        ehrs.put(ehr.getRecordID(), ehr);
        saveAllEHRs();
    }

    public EHR loadEHR(String recordID) throws IOException, ClassNotFoundException {
        loadAllEHRs();
        return ehrs.get(recordID);
    }

    private void saveAllEHRs() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(EHRS_FILE))) {
            oos.writeObject(new ArrayList<>(ehrs.values()));
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAllEHRs() throws IOException, ClassNotFoundException {
        File file = new File(EHRS_FILE);
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(EHRS_FILE))) {
            List<EHR> loadedEHRs = (List<EHR>) ois.readObject();
            ehrs.clear();
            for (EHR ehr : loadedEHRs) {
                ehrs.put(ehr.getRecordID(), ehr);
            }
        }
    }


    public void saveBill(Bill bill) throws IOException {
        if (bill == null) {
            throw new IllegalArgumentException("Bill cannot be null");
        }
        bills.put(bill.getBillID(), bill);
        saveAllBills();
    }

    public Bill loadBill(String billID) throws IOException, ClassNotFoundException {
        loadAllBills();
        return bills.get(billID);
    }

    private void saveAllBills() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BILLS_FILE))) {
            oos.writeObject(new ArrayList<>(bills.values()));
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAllBills() throws IOException, ClassNotFoundException {
        File file = new File(BILLS_FILE);
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BILLS_FILE))) {
            List<Bill> loadedBills = (List<Bill>) ois.readObject();
            bills.clear();
            for (Bill bill : loadedBills) {
                bills.put(bill.getBillID(), bill);
            }
        }
    }


    public void saveFeedback(Feedback feedback) throws IOException {
        if (feedback == null) {
            throw new IllegalArgumentException("Feedback cannot be null");
        }
        feedbacks.put(feedback.getFeedbackID(), feedback);
        saveAllFeedbacks();
    }

    public List<Feedback> getAllFeedbacks() throws IOException, ClassNotFoundException {
        loadAllFeedbacks();
        return new ArrayList<>(feedbacks.values());
    }

    private void saveAllFeedbacks() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FEEDBACKS_FILE))) {
            oos.writeObject(new ArrayList<>(feedbacks.values()));
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAllFeedbacks() throws IOException, ClassNotFoundException {
        File file = new File(FEEDBACKS_FILE);
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FEEDBACKS_FILE))) {
            List<Feedback> loadedFeedbacks = (List<Feedback>) ois.readObject();
            feedbacks.clear();
            for (Feedback feedback : loadedFeedbacks) {
                feedbacks.put(feedback.getFeedbackID(), feedback);
            }
        }
    }


    public void savePrescription(Prescription prescription) throws IOException {
        if (prescription == null) {
            throw new IllegalArgumentException("Prescription cannot be null");
        }
        prescriptions.put(prescription.getPrescriptionID(), prescription);
        saveAllPrescriptions();
    }

    private void saveAllPrescriptions() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PRESCRIPTIONS_FILE))) {
            oos.writeObject(new ArrayList<>(prescriptions.values()));
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAllPrescriptions() throws IOException, ClassNotFoundException {
        File file = new File(PRESCRIPTIONS_FILE);
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PRESCRIPTIONS_FILE))) {
            List<Prescription> loadedPrescriptions = (List<Prescription>) ois.readObject();
            prescriptions.clear();
            for (Prescription prescription : loadedPrescriptions) {
                prescriptions.put(prescription.getPrescriptionID(), prescription);
            }
        }
    }


    public List<Patient> getAllPatients() throws IOException, ClassNotFoundException {
        loadAllPatients();
        return new ArrayList<>(patients.values());
    }

    public List<Provider> getAllProviders() throws IOException, ClassNotFoundException {
        loadAllProviders();
        return new ArrayList<>(providers.values());
    }

    public List<Appointment> getAllAppointments() throws IOException, ClassNotFoundException {
        loadAllAppointments();
        return new ArrayList<>(appointments.values());
    }

    public List<EHR> getAllEHRs() throws IOException, ClassNotFoundException {
        loadAllEHRs();
        return new ArrayList<>(ehrs.values());
    }

    public List<Bill> getAllBills() throws IOException, ClassNotFoundException {
        loadAllBills();
        return new ArrayList<>(bills.values());
    }

    public List<Admin> getAllAdmins() throws IOException, ClassNotFoundException {
        loadAllAdmins();
        return new ArrayList<>(admins.values());
    }
}

