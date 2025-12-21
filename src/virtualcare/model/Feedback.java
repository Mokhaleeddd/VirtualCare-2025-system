package virtualcare.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Feedback implements Serializable {
    private static final long serialVersionUID = 1L;
    private String feedbackID;
    private int rating;
    private String comment;
    private String date;
    private Patient patient;

    public Feedback(String feedbackID, int rating, String comment) {
        this.feedbackID = feedbackID;
        this.rating = rating;
        this.comment = comment;
        this.date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public void submit() {
        System.out.println("Feedback " + feedbackID + " submitted with rating: " + rating);
    }

    public void analyze() {
        System.out.println("Analyzing feedback " + feedbackID + ": Rating=" + rating + ", Comment=" + comment);
    }

    public String getFeedbackID() {
        return feedbackID;
    }

    public void setFeedbackID(String feedbackID) {
        this.feedbackID = feedbackID;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "feedbackID='" + feedbackID + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}

