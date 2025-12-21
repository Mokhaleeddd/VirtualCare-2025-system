package virtualcare.model;

import java.io.Serializable;

public class Bill implements Serializable {
    private static final long serialVersionUID = 1L;
    private String billID;
    private double amount;
    private String status;
    private String paymentMethod;
    private Appointment appointment;

    public Bill(String billID, double amount, String status, String paymentMethod) {
        this.billID = billID;
        this.amount = amount;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

    public void generate() {
        System.out.println("Bill " + billID + " generated for amount: $" + amount);
    }

    public void pay() {
        this.status = "Paid";
        System.out.println("Bill " + billID + " paid via " + paymentMethod);
    }

    public String getBillID() {
        return billID;
    }

    public void setBillID(String billID) {
        this.billID = billID;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billID='" + billID + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}

