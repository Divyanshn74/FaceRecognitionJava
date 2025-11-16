# Face Recognition Attendance System (Java Backend)

This repository contains the **FaceRecognitionJava** Spring Boot application — the Java backend and web UI for a facial recognition attendance system. The face recognition logic runs in a separate Python service (face-engine-python); that service has its own README. This document covers **only** the Java project setup, configuration, and how to connect it to your face engine API.

---

## 🧩 What this service provides

- REST API for student registration and attendance
- Simple HTML frontend for registering students and marking attendance
- Persistence layer using Spring Data JPA (MySQL)
- A `FaceEngineService` that communicates with the external face-engine HTTP API

---

## 🏗️ Project Structure (Java only)

```
FaceRecognitionJava-main/
├── pom.xml
├── mvnw, mvnw.cmd
├── src/
│   ├── main/java/com/example/facerecog/
│   │   ├── controller/               # REST Controllers (StudentController, etc.)
│   │   ├── dto/                      # Request/Response DTOs (AttendanceRequest, ...)
│   │   ├── model/                    # JPA Entities (Student, Attendance)
│   │   ├── repository/               # Spring Data Repositories
│   │   ├── service/                  # Business logic including FaceEngineService
│   │   └── FacerecogApplication.java # Main Spring Boot application class
│   └── resources/
│       ├── static/                   # HTML pages (index, register, attendance)
│       └── application.properties    # Application configuration
└── src/test/java/...                 # Unit tests
```

---

## ⚙️ Prerequisites

- Java 17 (or the version specified in `pom.xml`) installed and `JAVA_HOME` set
- Maven 3.6+ (or use the included Maven wrapper `mvnw`)
- MySQL (or compatible DB) accessible for the application
- The Python face engine service (runs separately) — configure its URL in `application.properties` (default: `http://127.0.0.1:5001`)

---

## 🚀 Java Project Setup & Run (Step-by-step)

### 1. Clone the Java project
```bash
git clone <your-java-repo-url> FaceRecognitionJava-main
cd FaceRecognitionJava-main
```

> If you already have the repo files locally (example: unzip or copy), just `cd` into the project directory.

### 2. Create / Configure the database
Create a MySQL database for the app (example name `face_recog`):
```sql
CREATE DATABASE face_recog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Create a database user or use an existing one, then grant privileges:
```sql
CREATE USER 'faceuser'@'localhost' IDENTIFIED BY 'strong_password';
GRANT ALL PRIVILEGES ON face_recog.* TO 'faceuser'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configure `application.properties`
Open `src/main/resources/application.properties` and set your DB and face-engine URL values. Example:
```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/face_recog?useSSL=false&serverTimezone=UTC
spring.datasource.username=faceuser
spring.datasource.password=strong_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Face Engine API URL (the Python face-engine runs separately, default on port 5001)
face.engine.url=http://127.0.0.1:5001
```

> `spring.jpa.hibernate.ddl-auto=update` is convenient for development (it creates/updates tables automatically). For production, prefer explicit migrations (Flyway/Liquibase) and set this to `validate` or remove it.

### 4. Build the project
Use Maven wrapper (recommended) or your local Maven installation:
```bash
# Unix / macOS
./mvnw clean package -DskipTests

# Windows (PowerShell)
.\mvnw.cmd clean package -DskipTests
```

To run tests instead of skipping:
```bash
./mvnw test
```

### 5. Run the application
You can run the packaged JAR or start via Spring Boot plugin:
```bash
# Run with Spring Boot (during development)
./mvnw spring-boot:run

# OR run the packaged jar
java -jar target/*.jar
```

The application starts on port `8080` by default. Access the UI at:
```
http://localhost:8080
```

### 6. Verify integration with Face Engine
- Ensure the Python face engine service is running at the URL you configured (default `http://127.0.0.1:5001`).
- Use the web UI (`/register` page) to register a student image and then use the attendance page to mark attendance.
- The Java backend will forward face image data to the face-engine endpoints (example endpoints: `/register-face`, `/recognize` — check the Python repo README for exact routes).

---

## 🔧 Common tweaks & notes

- If your face engine is hosted on another machine or port, change `face.engine.url` accordingly. Use `http://<host>:5001` or the deployed HTTPS URL.
- If you face CORS issues during local development (front-end JS calling `localhost:8080` + Java calling Python), ensure the Java backend allows cross-origin requests or the Python API allows them as needed.
- For production, secure your endpoints, use HTTPS, set proper secrets management, and replace `ddl-auto=update` with migrations.

---

## 📄 API Endpoints (high-level overview)

| Java Endpoint | Purpose |
|---------------|---------|
| `/api/student/register` | Register student and forward face image to face-engine for encoding/storage |
| `/api/student/mark-attendance` | Send face image to face-engine to identify and then save attendance |
| `/api/student/attendance/{id}` | Retrieve attendance history for a student |

Check the controller classes in `src/main/java/com/example/facerecog/controller` for exact request/response formats.

---


### 🚀 Core System & Security Enhancements
- **Role-Based Access Control (RBAC):** Implemented a robust three-tier security system with `ROLE_MASTER`, `ROLE_ADMIN`, and `ROLE_USER`.
- **Themed UI:** Overhauled the entire web UI with a consistent "Monet pastel" glassmorphism theme.
- **User-Student Linking:** Explicitly linked `User` accounts to `Student` records via an `enrollmentNumber` field.
- **Custom Error Handling:** Implemented a custom 403 "Access Denied" page.
- **Forced Password Change:** New users (especially those from bulk imports) are now required to change their default password on first login for enhanced security.
- **Dynamic Application Settings:** Introduced a mechanism to store and retrieve application settings (e.g., Python API URL) from the database, allowing dynamic configuration without application restarts.
- **Robustness Improvements:** Addressed various compilation errors and runtime bugs, including `LazyInitializationException` and form binding issues.

### 👑 ROLE_MASTER Features
- **Master Dashboard:** A comprehensive dashboard providing full oversight and management capabilities.
- **User Management (CRUD):** Full Create, Read, Update, Delete functionality for user accounts, including role assignment and `enrollmentNumber` linking.
- **Student Management (CRUD):** Full Create, Read, Update, Delete functionality for student records.
- **Bulk User Import:** Ability to import multiple user accounts from a CSV file, assigning default passwords and roles.
- **Bulk Student Import:** Ability to import multiple student records from a CSV file, with face embeddings initially set to `NULL`.
- **"Register Faces" Workflow:** Dedicated page to list all students and allow masters to register or update face embeddings via file upload or webcam capture.
- **Global Attendance View & Edit:** A page to view and search attendance records for *all* students across any date range, with the ability to modify individual attendance statuses.
- **Application Settings UI:** A dedicated UI to manage and update core application settings dynamically.

### 👮‍♀️ ROLE_ADMIN Features
- **Manual Attendance Management:** A new page allowing admins to manually mark or update attendance for all students on a specific date, with "Mark All Present" and "Mark All Absent" options.
- **PDF Attendance Reports:** Ability to generate and download attendance reports in PDF format, filterable by student and date range.
- **"Register Faces" Workflow:** Dedicated page to list all students and allow admins to register or update face embeddings via file upload or webcam capture.

### 👤 ROLE_USER Features
- **User Profile Page:** A dedicated page for users to view their own profile information and securely change their password.
- **Attendance Percentage:** The user's attendance records page now displays the overall attendance percentage based on the applied filters.

---

## 🧑‍💻 Authors & Contributors
 
- 👨‍💻 **Divyansh Namdev** (Divyanshn74)

Python face engine repo: https://github.com/Divyanshn74/face-engine-python

---

## 🪪 License

This project is licensed under the **MIT License** – see the [LICENSE](LICENSE) file for details.
