# Face Recognition Attendance System

A **Spring Boot–based facial recognition attendance system** designed to automate student attendance tracking using face recognition. The project integrates **computer vision**, **RESTful APIs**, and a **web interface** for registration, recognition, and attendance management.

---

## 📋 Features

- **Student Registration:** Register new students with name, ID, and face image.  
- **Facial Recognition:** Automatically identifies and marks attendance through camera input.  
- **Attendance Management:** Stores attendance records with timestamps.  
- **RESTful API Backend:** Provides structured endpoints for registration and attendance operations.  
- **Frontend UI:** Simple HTML interface for registration and attendance marking.  

---

## 🏗️ Project Structure

```
FaceRecognitionJava-main/
│
├── pom.xml                                # Maven build configuration
├── src/
│   ├── main/
│   │   ├── java/com/example/facerecog/
│   │   │   ├── controller/               # REST Controllers
│   │   │   ├── dto/                      # Request/Response DTOs
│   │   │   ├── model/                    # JPA Entities
│   │   │   ├── repository/               # Spring Data Repositories
│   │   │   ├── service/                  # Business Logic & Face Recognition
│   │   │   └── FacerecogApplication.java # Main Spring Boot Class
│   │   └── resources/
│   │       ├── static/                   # Frontend HTML pages
│   │       └── application.properties     # Configuration file
│   └── test/java/...                     # Unit tests
│
└── HELP.md, mvnw, mvnw.cmd               # Maven helper scripts
```

---

## ⚙️ Technologies Used

| Category | Tools/Frameworks |
|-----------|------------------|
| **Backend** | Java 17+, Spring Boot |
| **Frontend** | HTML, JavaScript, Bootstrap |
| **Database** | MySQL (JPA/Hibernate) |
| **Face Recognition** | OpenCV / JavaCV (Bytedeco) |
| **Build Tool** | Maven |
| **Server** | Embedded Tomcat |

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/FaceRecognitionJava.git
cd FaceRecognitionJava-main
```

### 2. Configure Database

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/face_recog
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

### 3. Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

Access the app at:  
👉 **http://localhost:8080**

---

## 🧠 Core Components

- **FaceEngineService:** Handles facial feature extraction and comparison.  
- **StudentService:** Manages student data and registration logic.  
- **AttendanceService:** Manages marking and retrieving attendance records.  
- **StudentController:** REST endpoints for student operations and attendance marking.

---

## 📄 API Endpoints

| Endpoint | Method | Description |
|-----------|---------|-------------|
| `/api/student/register` | `POST` | Register a new student |
| `/api/student/mark-attendance` | `POST` | Mark attendance using face image |
| `/api/student/attendance/{id}` | `GET` | Get attendance history of a student |

---

## 💡 Future Enhancements

- Real-time camera integration for live attendance.  
- Admin dashboard with analytics and reporting.  
- Multi-class / course attendance tracking.  
- Enhanced facial recognition accuracy using deep learning models.

---

## 🧑‍💻 Author

Project developed by **{Your Name}**  
Feel free to connect and contribute!

---

## 🪪 License

This project is licensed under the **MIT License** – see the [LICENSE](LICENSE) file for details.
