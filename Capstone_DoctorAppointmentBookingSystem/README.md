# MediBook — Healthcare Appointment Booking Platform

MediBook is a full-stack doctor-patient appointment booking web application. It bridges the gap between healthcare practitioners and patients by automating slots scheduling, reservation checkouts, and medical leave/bulk cancellation request approvals with role-based dashboard consoles.

---

##  Technology Stack

* **Frontend:**
  - **React.js** (React Scripts / CRA build system)
  - **CSS:** Vanilla CSS & Bootstrap Icons
  - **HTTP Client:** Axios Interceptors (with JWT authorization header context)
  - **Routing:** React Router DOM (Declarative routing & Protected route wrappers)
  - **Alerts:** React-Toastify notifications

* **Backend:**
  - **FastAPI** (Python 3.13)
  - **ODM Layer:** Beanie ODM (Async Object Document Mapper for MongoDB)
  - **Database:** MongoDB
  - **Testing:** Pytest (with AsyncIO, pytest-cov, and process-isolated database configurations)

---

##  Key Functional Features & Workflows

### 1. User & Profile Authentication
* **Role-Based Access Control:** Separate login, registration, and dashboard environments for **Patients**, **Doctors**, and **Administrators**.
* **Doctor Verification Gatekeeper:** When a doctor registers, their status is set to `PENDING` by default. They cannot log in until verified and `APPROVED` by an Admin.
* **Profile Management:** Patients and Doctors can update basic contact fields, change account passwords, or configure profile photos and consultation fee details.
* **Account Deactivation:** Doctors can toggle their profile visibility to `Inactive` from their profile, hiding their slots from Patients. Reactivation can be self-performed by logging in again and toggling the status back to `Active`.

### 2. Slots Scheduling (Doctor Dashboard)
* **Creation Rules:** Doctors define slot dates and time intervals (Start to End). 
* **Validation Guards:** Backend prevents scheduling slots on past dates, or creating slots where the end time is before the start time.
* **Conflict Resolution:** System prevents slot collisions (overlapping schedules for the same doctor).

### 3. Search & Directory Listings (Patient Dashboard)
* **Search Filters:** Patients can filter doctors list by name, specialization area, rating counts, or clinic locations.
* **Availability Mapping:** Only active and approved doctors with available (`SlotStatus.AVAILABLE`) schedule slots are displayed for booking.

### 4. Appointment Booking & Cancellations
* **Real-time Reservations:** Patients select a slot and book it, changing the slot status to `BOOKED` and creating a pending transaction.
* **Cancellation Policies:** Patients can cancel a confirmed appointment up to **2 hours** before the scheduled time. In contrast, Doctors manage scheduling issues via Leave Requests.
* **Bulk Cancellations (Leave Requests):** If a doctor is unavailable, they submit a Leave Request with a specific reason. Once approved by the Admin, the system automatically cancels all affected appointments, marks slots as `CANCELLED`, and releases notifications.

### 5. Mock Payment Processing
* **Checkout Simulation:** Simulates card validations (CVV length, Card numbers length).
* **Double Checkout Prevention:** Core validation layer checks database logs and blocks double payment transactions for the same appointment, and prevents transactions on already cancelled appointments.

### 6. Admin Panel Operations
* **Analytics Indicators:** Core dashboard widgets tracking total users counts, approved practitioners, and appointments status distributions.
* **Credentials Review:** Admin reviews pending doctor registrations and approves/rejects them.
* **Leave Requests Manager:** Admin reviews, approves, or rejects doctors' leave requests.

---

##  Future Enhancements

- **Real Payment Gateway Integration:** Transition from mock simulation cards checkouts to actual Payment Gateways (Stripe / Razorpay API hooks).
- **Integrated Video Consultation:** Real-time patient-doctor video consultations using WebRTC or Zoom Web SDKs.
- **E-Prescriptions & Electronic Health Records (EHR):** Provide doctors with UI modules to write digital prescriptions and securely upload patient diagnostic records.
- **Automated Alerts & Push Notifications:** Auto-send SMS, Email or WhatsApp reminders to patients 1 hour before scheduled slots.
- **Advanced Filtering:** Location-based nearby search using GPS coordinates and maps platforms.

---

##  API Documentation & Endpoints

FastAPI automatically generates interactive API documentation. You can access it locally once the backend server is running:
* **Interactive Swagger UI:** [http://localhost:8000/docs](http://localhost:8000/docs)
* **ReDoc Documentation:** [http://localhost:8000/redoc](http://localhost:8000/redoc)

### Core Router Modules:
* **Authentication (`/auth`):** Registration, login, profile updates, and password changes.
* **Doctor Profile (`/doctors`):** Retrieve practitioner schedules, update specialization credentials.
* **Slots (`/slots`):** Query slot availabilities, bulk slots scheduling updates.
* **Appointments (`/appointments`):** Book slot reservations, cancel appointments, view user schedules.
* **Payments (`/payments`):** Mock checkout validations and payments status transitions.
* **Admin Management (`/admin`):** Approve/Reject doctor applications, review leave requests, dashboard statistics.

---

##  Database Schema & Collections

MediBook uses MongoDB with Beanie ODM. The dynamic database contains the following collection documents:
1. **`users`:** Holds user profiles, hashed passwords, access roles (`PATIENT`, `DOCTOR`, `ADMIN`), activation flags, and admin approval status.
2. **`doctor_profiles`:** Connects to `users` collection via reference ID. Contains qualifications, experience details, consultation fees, specialization tags, and clinic coordinates.
3. **`slots`:** Stores individual doctor schedules mapped by date, start-end intervals, and allocation status (`AVAILABLE`, `BOOKED`, `CANCELLED`).
4. **`appointments`:** Links a patient ID and doctor ID to a reserved slot. Stores current booking status (`CONFIRMED`, `COMPLETED`, `ABSENT`, `CANCELLED`) and cancellation logs.
5. **`payments`:** Logs simulated transactions containing card patterns validations, transaction timestamp, payment status (`PENDING`, `SUCCESS`, `FAILED`), and amount.

---

##  Project Structure

```
Capstone_DoctorAppointmentBookingSystem/
├── backend/
│   ├── constants/             # Error messages templates & app settings
│   ├── database/              # DB connection rules & async client
│   ├── dependencies/          # JWT tokens validation & role gatekeepers
│   ├── enums/                 # Application status configurations
│   ├── exceptions/            # Global HTTP handlers & custom exception templates
│   ├── models/                # Database documents schemas (User, Slot, Payment, etc.)
│   ├── repositories/          # Database collection CRUD abstractions
│   ├── routers/               # API endpoints controllers
│   ├── schemas/               # Request payloads & Response objects
│   ├── services/              # Pure business logic implementation
│   ├── tests/                 # Integration test suite (48 test cases)
│   ├── utils/                 # Password crypts & token signers
│   ├── validators/            # Input validator methods
│   └── main.py                # App entrypoint
└── frontend/
    ├── public/                # Static assets & index.html template
    ├── src/
    │   ├── api/               # API endpoint clients
    │   ├── assets/            # Global CSS styles
    │   ├── components/        # Modals, loaders & helper components
    │   ├── context/           # Global authentication context
    │   ├── layouts/           # Dashboard shell & Auth shell layouts
    │   ├── pages/             # Pages views organized by role (Admin/Doctor/Patient/Shared)
    │   ├── routes/            # Route path strings
    │   ├── utils/             # Dates & tokens formatting scripts
    │   ├── index.js           # Client mount entrypoint
    │   └── App.jsx            # Main route definition wrappers
    ├── package.json           # Node packages dependencies
    └── index.html             # React mount node template
```

---

##  Setup & Installation Guide

### 1. Backend Server Setup
1. **Navigate to the backend directory:**
   ```bash
   cd backend
   ```
2. **Initialize a virtual environment:**
   ```bash
   python -m venv venv
   ```
3. **Activate the environment:**
   - **macOS/Linux:**
     ```bash
     source venv/bin/activate
     ```
   - **Windows (Command Prompt):**
     ```cmd
     venv\Scripts\activate
     ```
4. **Install backend packages:**
   ```bash
   pip install -r requirements.txt
   ```
5. **Configure environment variables:**
   Create a `.env` file in the `backend/` folder:
   ```env
   MONGODB_URL=mongodb://localhost:27017
   DB_NAME=doctor_appointment_db
   JWT_SECRET=your_jwt_signature_secret_key
   ```
6. **Start the backend development server:**
   ```bash
   uvicorn main:app --reload
   ```
   *Backend is live at: `http://localhost:8000`*

---

### 2. Run Automated Test Suite
To clean python cache and run the backend integration tests:
```bash
find . -name "*.pyc" -delete && find . -name "__pycache__" -delete
PYTHONPATH=. venv/bin/pytest --cov=. tests/
```

---

### 3. Frontend Client Setup
1. **Navigate to the frontend directory:**
   ```bash
   cd ../frontend
   ```
2. **Install node dependencies:**
   ```bash
   npm install
   ```
3. **Launch the development server:**
   ```bash
   npm start
   ```
   *Frontend application will launch at: `http://localhost:3000`*

---

##  Author & Credits

* **Author:** Mahak Dhanotiya
* **Project Type:** Capstone Doctor Appointment Booking System 
* **Repository:** NucleusTeq-Assignments

