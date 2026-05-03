# Interview Process Tracking System (Capstone Project)

A professional, enterprise-grade recruitment lifecycle management system designed to streamline the journey from job creation to final selection. This application provides a seamless, secure, and automated experience for HR teams, Candidates, and Interview Panelists.

---

## 1. Project Overview

The **Interview Process Tracking System** is a full-stack solution built to automate and optimize the recruitment workflow. It eliminates manual tracking by providing a centralized platform where HR can manage job descriptions, monitor candidate progress through multiple stages, and collect structured feedback from interview panels. 

With **role-based access control** and **automated email notifications**, the system ensures that every stakeholder has the right information at the right time, maintaining process integrity and professional standards.

---

## 2. Key Features

### HR Management
*   **Dynamic Job Management**: Create, edit, and manage Job Descriptions (JDs) with specific requirements.
*   **Centralized Recruitment Hub**: Full control over candidate tracking and final verdict (Selection/Rejection).
*   **Smart Scheduling**: Assign up to 2 panel members per interview round and manage timing with future-date validation.

### Candidate Experience
*   **Seamless Application**: Easy profiling with automated resume (PDF) upload and identity verification.
*   **Transparent Tracking**: Dedicated dashboard to monitor application status (Profiling → Screening → L1 → L2 → HR).
*   **Data Integrity**: Auto-filling registration data with identity-locking for secure submissions.

### Panelist & Feedback
*   **Structured Evaluation**: Dedicated portal to submit ratings and detailed comments for every round.
*   **Onboarding**: Token-based secure password setup for new panel members.
*   **Feedback Confidentiality**: Feedback remains hidden from candidates to maintain interview objectivity.

### System Automation
*   **Instant Notifications**: Automated emails for scheduling, rescheduling, and final outcomes.
*   **Logic Guards**: Strict enforcement of round order (e.g., L1 must be completed before L2).

---

## 3. Project Structure

The project follows a clean, layered architecture (Controller-Service-Repository) for maximum scalability:

```text
/Interview-Tracking-System
├── /backend
│   ├── /src/main/java/com/mahak/capstone/interviewprocesstrackingsystem
│   │   ├── /controller       # REST Endpoints (Auth, Candidate, Job, Interview, etc.)
│   │   ├── /service          # Business Logic Layer (Interfaces & Implementations)
│   │   ├── /repository       # JPA Repositories (PostgreSQL Integration)
│   │   ├── /entity           # JPA Database Entities
│   │   ├── /dto              # Data Transfer Objects (Requests & Responses)
│   │   ├── /mapper           # DTO-Entity Mapping Logic
│   │   ├── /validation       # Custom Business Rules & Input Validation
│   │   ├── /security         # JWT Config, UserDetailsService, Auth Filters
│   │   ├── /exception        # Global Error Handling & Custom Exceptions
│   │   ├── /enums            # System Enums (JobType, InterviewStage, etc.)
│   │   └── /constants        # Centralized Error & API Constants
│   └── /src/test/java        # 151+ JUnit & Mockito Test Cases
└── /frontend
    ├── /src/pages            # HTML Views (HR, Candidate, Panel Dashboards)
    ├── /src/scripts
    │   ├── /actions          # Modular API Action Triggers
    │   ├── /lib/handlers     # Fetch Handler & Request Interceptors
    │   └── /lib/utils        # UI Rendering & Form Utilities
    └── /src/styles           # CSS Design System (Global, Components, Pages)
```

---

## 4. API Documentation

### Authentication Module (`/auth`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| POST | `/register` | Create a new user account (Candidate/HR) |
| POST | `/login` | Authenticate and receive JWT token |
| POST | `/set-password` | Initial password setup for new Panelists |
| GET | `/me` | Fetch currently authenticated user details |

### Candidate Module (`/candidates`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| POST | `/` | Submit a new job application |
| PUT | `/update` | Update candidate profile details |
| GET | `/my-profile` | Fetch logged-in candidate's profile |
| GET | `/all` | [HR] List all candidate applications |
| GET | `/{id}` | [HR] Fetch specific candidate details |

### Job Module (`/jobs`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| POST | `/` | [HR] Create a new Job Description |
| PUT | `/{id}` | [HR] Update existing Job details |
| GET | `/all` | Fetch all active/inactive job listings |
| POST | `/{id}/activate` | Toggle job status to Active |
| POST | `/{id}/deactivate` | Toggle job status to Inactive |

### Interview Module (`/api/interviews`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| POST | `/schedule` | [HR] Schedule a new interview round |
| GET | `/all` | List all interviews (Filtered by role) |
| GET | `/candidate/{id}` | Fetch interview history for a candidate |
| PUT | `/{id}/update` | Reschedule or modify interview details |
| PATCH | `/{id}/status` | Update status (Scheduled -> In Progress -> Completed) |
| POST | `/stage-progression` | [HR] Move candidate to the next round |

### Feedback Module (`/api/feedback`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| POST | `/submit` | [Panel/HR] Submit technical/behavioral evaluation |
| GET | `/interview/{id}` | Fetch all feedback for a specific round |
| GET | `/candidate/{id}` | Fetch full feedback history for a candidate |

---

## 5. Tech Stack & Environment

*   **Java**: 17 (Amazon Corretto)
*   **Spring Boot**: 3.5.13
*   **Build Tool**: Maven
*   **Database**: PostgreSQL 15+
*   **Security**: Spring Security + JWT
*   **Frontend**: JavaScript (ES6+), Vanilla CSS3, HTML5
*   **Mail**: Java Mail Sender (SMTP)
*   **Testing**: JUnit 5, Mockito, JaCoCo (81%+ Coverage)

---

## 6. Setup & Installation

### 1. Database Setup
Create a PostgreSQL database:
```sql
CREATE DATABASE interview_db;
```

### 2. Backend Configuration
Update `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/interview_db
spring.datasource.username=your_username
spring.datasource.password=your_password

# SMTP for Emails
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```

### 3. Build & Run
```bash
cd backend/interview-process-tracking-system
mvn clean package
java -jar target/interview-process-tracking-system-0.0.1-SNAPSHOT.jar
```

### 4. Frontend Access
Open `frontend/src/pages/index.html` using a **Live Server** (VS Code) or any static file server.

---

## 7. Quality Assurance
The project ensures reliability through 151+ automated test cases. Run tests using:
```bash
mvn test
```
To view the coverage report, check: `target/site/jacoco/index.html`.

---

## Developer
**Mahak Dhanotiya**
*NucleusTeq Intern | Capstone Project*


---

**Developed for NucleusTeq Capstone Project - Focusing on Scalability, Security, and Quality.**
