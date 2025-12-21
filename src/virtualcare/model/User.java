package virtualcare.model;

import java.io.Serializable;


public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String userID;
    protected String name;
    protected String password;

    public User(String userID, String name) {
        this.userID = userID;
        this.name = name;
        this.password = ""; // Default empty password
    }

    public User(String userID, String name, String password) {
        this.userID = userID;
        this.name = name;
        this.password = password;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean verifyPassword(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }

    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

