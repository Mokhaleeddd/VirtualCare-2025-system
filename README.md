# VirtualCare 2025 - Healthcare Management System

A comprehensive healthcare management system built with Java Swing, implementing a three-tier architecture for managing patients, healthcare providers, and administrative operations.

## Table of Contents
- [Overview](#overview)
- [System Architecture](#system-architecture)
- [Features](#features)
- [User Roles](#user-roles)
- [Getting Started](#getting-started)
- [Default Accounts](#default-accounts)
- [Project Structure](#project-structure)
- [Technology Stack](#technology-stack)

## Overview

VirtualCare 2025 is a desktop-based healthcare management system designed to streamline healthcare operations, patient management, appointment scheduling, electronic health records (EHR), billing, and feedback collection. The system follows a three-tier architecture pattern with clear separation between presentation, business logic, and data layers.

## System Architecture

The system implements a **three-tier architecture**:

### 1. Presentation Layer
- **Patient Interface**: Patient-facing GUI for registration, appointments, EHR viewing, and feedback
- **Provider Interface**: Healthcare provider dashboard for schedule management, consultations, and prescriptions
- **Admin Dashboard**: Administrative panel for system management, provider configuration, billing, and analytics

### 2. Logic Layer (Business Services)
- **Authentication & Authorization Service**: Manages user authentication and role-based access control
- **Appointment Manager**: Handles appointment scheduling and management
- **Billing Manager**: Manages billing processes and financial transactions
- **EHR Manager**: Manages Electronic Health Records
- **Prescription Manager**: Handles prescription creation and management
- **Feedback Analyzer**: Processes and analyzes user feedback
- **Consultation Service**: Manages consultation-related functionalities

### 3. Data Layer
- **Data Manager**: Centralized data persistence service using Java serialization
- **Storage**: All data persisted in `data/` directory as binary files
- **Entities**: Patient Database, Provider Database, EHR Repository, Billing Database, Feedback Database

## Features

### Authentication & Authorization
- Secure login system with user ID and password authentication
- Role-based access control (RBAC) with three user types: Patient, Provider, Admin
- Session management with logout functionality
- Password-protected accounts for all user types
- User registration system

### Patient Features
- **Registration & Profile Management**: Register new accounts and update personal information
- **Appointment Booking**: Schedule appointments with healthcare providers, select date/time
- **EHR Viewing**: Access and view Electronic Health Records
- **Feedback System**: Submit ratings and comments about services
- **Appointment Management**: View and manage personal appointments

### Provider Features
- **Schedule Management**: Update availability and manage schedule
- **Consultation Management**: Conduct consultations with patients
- **Prescription Management**: Issue prescriptions to patients
- **Appointment Viewing**: View and manage provider's appointments
- **Patient Search**: Search for patients by name, ID, or contact info

### Admin Features
- **Provider Configuration**: Create and manage healthcare provider accounts
- **Bill Generation**: Generate bills for appointments
- **Provider Management**: View all providers, delete provider accounts
- **Bill Management**: View all bills in the system
- **Feedback Management**: View and analyze patient feedback

### Data Management
- Persistent storage using Java object serialization
- Automatic ID generation for all entities
- Data search and filtering capabilities
- Comprehensive CRUD operations

## User Roles

### Patient
- Can register and create account
- Book appointments with providers
- View their own EHR
- Submit feedback
- Manage personal appointments

### Provider (Healthcare Provider)
- Manage schedule and availability
- Conduct consultations
- Issue prescriptions
- View assigned appointments
- Search for patients

### Admin (Administrator)
- Create and manage provider accounts
- Generate bills for appointments
- View system-wide data (providers, bills, feedback)
- Manage provider accounts (create, view, delete)

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Any Java IDE (IntelliJ IDEA, Eclipse, NetBeans) or command-line compiler

### Running the Application

1. **Clone or download the project**
   ```bash
   cd VirtualCare-2025-system
   ```

2. **Compile the Java files**
   ```bash
   javac -d out -sourcepath src src/virtualcare/gui/MainGUI.java
   ```

3. **Run the application**
   ```bash
   java -cp out virtualcare.gui.MainGUI
   ```

   Or using an IDE:
   - Open the project in your IDE
   - Navigate to `src/virtualcare/gui/MainGUI.java`
   - Run the `main()` method

### First Time Setup

On first launch, the system automatically creates default admin and provider accounts (see [Default Accounts](#default-accounts) below).

## Default Accounts

For testing and initial system access, the following default accounts are automatically created:

### Admin Account
- **User ID**: `ADM1`
- **Password**: `admin123`
- **Name**: Main Admin
- **Capabilities**: Full system access, provider management, bill generation

### Provider Account
- **User ID**: `PROV1`
- **Password**: `provider123`
- **Name**: Dr. Main Provider
- **Specialty**: General Practice
- **Availability**: Monday-Friday 9AM-5PM

### Patient Registration
- Patients can register through the login screen by clicking "New user? Click here to register"
- No default patient accounts are created
- Patient IDs are auto-generated (format: PAT1, PAT2, etc.)

## Project Structure

```
VirtualCare-2025-system/
├── src/
│   └── virtualcare/
│       ├── gui/                    # Presentation Layer
│       │   ├── MainGUI.java        # Main application window
│       │   ├── LoginPanel.java     # Login and registration interface
│       │   ├── PatientPanel.java   # Patient interface
│       │   ├── ProviderPanel.java  # Provider interface
│       │   └── AdminPanel.java     # Admin dashboard
│       ├── model/                  # Data Models
│       │   ├── User.java           # Base user class
│       │   ├── Patient.java        # Patient model
│       │   ├── Provider.java       # Provider model
│       │   ├── Admin.java          # Admin model
│       │   ├── Appointment.java    # Appointment model
│       │   ├── Bill.java           # Bill model
│       │   ├── EHR.java            # Electronic Health Record model
│       │   ├── Prescription.java   # Prescription model
│       │   └── Feedback.java       # Feedback model
│       └── service/                # Business Logic Layer
│           ├── DataManager.java    # Data persistence service
│           └── AuthenticationService.java  # Authentication & authorization
├── data/                           # Data storage directory (auto-created)
│   ├── patients.dat
│   ├── providers.dat
│   ├── admins.dat
│   ├── appointments.dat
│   ├── bills.dat
│   ├── ehrs.dat
│   ├── feedbacks.dat
│   ├── prescriptions.dat
│   └── counters.dat
├── out/                            # Compiled classes
└── README.md                       # This file
```

## Technology Stack

- **Programming Language**: Java
- **GUI Framework**: Java Swing (javax.swing)
- **Data Persistence**: Java Object Serialization
- **Architecture Pattern**: Three-tier architecture (Presentation, Logic, Data)
- **Design Patterns**: 
  - MVC (Model-View-Controller) pattern
  - Singleton pattern (DataManager)
  - Factory pattern (ID generation)

## Key Components

### Models (Entity Classes)
- **User**: Abstract base class for all user types with password support
- **Patient**: Extends User, includes contact info, medical history, appointments, feedback
- **Provider**: Extends User, includes specialty, availability, managed appointments
- **Admin**: Extends User, manages provider accounts
- **Appointment**: Links patients and providers with date/time and status
- **Bill**: Associated with appointments, includes amount, status, payment method
- **EHR**: Electronic Health Records linked to patients
- **Prescription**: Issued by providers, linked to patients and EHRs
- **Feedback**: Patient ratings and comments

### Services
- **DataManager**: Handles all data persistence operations using file I/O
- **AuthenticationService**: Manages authentication, authorization, and session management

### GUI Components
- **MainGUI**: Main application frame with card layout for panel switching
- **LoginPanel**: Authentication interface with login and registration dialogs
- **PatientPanel**: Tabbed interface for patient operations
- **ProviderPanel**: Tabbed interface for provider operations
- **AdminPanel**: Tabbed interface for admin operations

## Security Features

- Password-based authentication
- Role-based access control
- User session management
- Secure password storage (stored in serialized objects)
- Authorization checks for data access

## Data Persistence

All data is persisted using Java object serialization:
- Files stored in `data/` directory
- Automatic data loading on application startup
- Data saved after each modification
- Backup recommended for production use

## Future Enhancements

Potential improvements for future versions:
- Database integration (MySQL, PostgreSQL)
- Password encryption/hashing
- Email notifications
- Appointment reminders
- Advanced reporting and analytics
- Multi-language support
- Cloud deployment options

## License

This project is provided as-is for educational and development purposes.

## Contact & Support

For issues, questions, or contributions, please refer to the project repository or contact the development team.

---

**Version**: 2025  
**Last Updated**: 2025  
**Status**: Production Ready
