package virtualcare.service;

import virtualcare.model.*;
import java.io.IOException;

/**
 * Authentication & Authorization Service
 * Handles user authentication and authorization checks
 */
public class AuthenticationService {
    private DataManager dataManager;
    private User currentUser;

    public AuthenticationService(DataManager dataManager) {
        this.dataManager = dataManager;
        this.currentUser = null;
    }

    /**
     * Authenticates a user based on userID and password
     * @param userID The user ID to authenticate
     * @param password The password to verify
     * @param userType The type of user (PATIENT, PROVIDER, ADMIN)
     * @return The authenticated User object, or null if authentication fails
     */
    public User authenticate(String userID, String password, UserType userType) {
        try {
            User user = null;

            switch (userType) {
                case PATIENT:
                    user = dataManager.loadPatient(userID);
                    break;
                case PROVIDER:
                    user = dataManager.loadProvider(userID);
                    break;
                case ADMIN:
                    user = dataManager.loadAdmin(userID);
                    break;
            }

            if (user != null && user.verifyPassword(password)) {
                this.currentUser = user;
                return user;
            }
        } catch (Exception e) {
            // Authentication failed
            return null;
        }
        return null;
    }

    /**
     * Checks if a user is authorized to access a specific resource
     * @param requiredRole The required role for access
     * @return true if authorized, false otherwise
     */
    public boolean isAuthorized(UserType requiredRole) {
        if (currentUser == null) {
            return false;
        }

        switch (requiredRole) {
            case PATIENT:
                return currentUser instanceof Patient;
            case PROVIDER:
                return currentUser instanceof Provider;
            case ADMIN:
                return currentUser instanceof Admin;
            default:
                return false;
        }
    }

    /**
     * Checks if current user is authorized to access patient-specific resources
     * @param patientID The patient ID to check authorization for
     * @return true if authorized, false otherwise
     */
    public boolean canAccessPatientData(String patientID) {
        if (currentUser == null) {
            return false;
        }

        // Admin and Providers can access all patient data
        if (currentUser instanceof Admin || currentUser instanceof Provider) {
            return true;
        }

        // Patients can only access their own data
        if (currentUser instanceof Patient) {
            return currentUser.getUserID().equals(patientID);
        }

        return false;
    }

    /**
     * Checks if current user is authorized to access provider-specific resources
     * @param providerID The provider ID to check authorization for
     * @return true if authorized, false otherwise
     */
    public boolean canAccessProviderData(String providerID) {
        if (currentUser == null) {
            return false;
        }

        // Admin can access all provider data
        if (currentUser instanceof Admin) {
            return true;
        }

        // Providers can only access their own data
        if (currentUser instanceof Provider) {
            return currentUser.getUserID().equals(providerID);
        }

        // Patients can view provider info (but not modify)
        return currentUser instanceof Patient;
    }

    /**
     * Checks if current user is authorized to perform administrative actions
     * @return true if authorized, false otherwise
     */
    public boolean canPerformAdminActions() {
        return currentUser instanceof Admin;
    }

    /**
     * Gets the currently authenticated user
     * @return The current user, or null if not authenticated
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Logs out the current user
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Checks if a user is currently authenticated
     * @return true if authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        return currentUser != null;
    }

    /**
     * Gets the user type of the currently authenticated user
     * @return The UserType, or null if not authenticated
     */
    public UserType getCurrentUserType() {
        if (currentUser == null) {
            return null;
        }

        if (currentUser instanceof Patient) {
            return UserType.PATIENT;
        } else if (currentUser instanceof Provider) {
            return UserType.PROVIDER;
        } else if (currentUser instanceof Admin) {
            return UserType.ADMIN;
        }

        return null;
    }

    /**
     * Enum for user types
     */
    public enum UserType {
        PATIENT,
        PROVIDER,
        ADMIN
    }
}

