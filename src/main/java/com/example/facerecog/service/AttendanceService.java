package com.example.facerecog.service;

import com.example.facerecog.dto.AttendanceResponse;
import com.example.facerecog.model.Attendance;
import com.example.facerecog.model.Student;
import com.example.facerecog.repository.AttendanceRepository;
import com.example.facerecog.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final FaceEngineService faceEngineService;

    public AttendanceService(StudentRepository studentRepository, AttendanceRepository attendanceRepository, FaceEngineService faceEngineService) {
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.faceEngineService = faceEngineService;
    }

    @Transactional
    public Map<String, String> markAttendance(String rollNo, String base64Image) {
        System.out.println("Attempting to mark attendance for rollNo: " + rollNo);

        Optional<Student> studentOptional = studentRepository.findByRollNo(rollNo);
        if (studentOptional.isEmpty()) {
            System.out.println("Student with rollNo " + rollNo + " not found.");
            return Map.of("status", "Student not found");
        }
        Student student = studentOptional.get();

        System.out.println("Comparing faces for student " + student.getName());
        boolean match = faceEngineService.compare(student.getFaceEmbedding(), base64Image);

        if (match) {
            System.out.println("Face matched for student " + student.getName() + ". Checking for existing attendance.");
            LocalDate today = LocalDate.now();
            Optional<Attendance> existingAttendance = attendanceRepository.findByStudentAndDate(student, today);

            if (existingAttendance.isEmpty()) {
                System.out.println("No existing attendance for " + student.getName() + " today. Marking as PRESENT.");
                Attendance attendance = Attendance.builder()
                        .student(student)
                        .date(today)
                        .status("PRESENT")
                        .build();
                attendanceRepository.save(attendance);
                return Map.of("status", "PRESENT");
            } else {
                System.out.println("Attendance already marked for " + student.getName() + " today.");
                return Map.of("status", "ALREADY_PRESENT");
            }
        } else {
            System.out.println("Face did not match for student " + student.getName());
            return Map.of("status", "NOT_MATCHED");
        }
    }

    public List<AttendanceResponse> getStudentAttendance(String enrollmentNumber) {
        Optional<Student> studentOptional = studentRepository.findByEnrollmentNumber(enrollmentNumber);
        if (studentOptional.isEmpty()) {
            return List.of(); // Return empty list if student not found
        }
        Student student = studentOptional.get();
        List<Attendance> attendanceRecords = attendanceRepository.findByStudent(student);

        return attendanceRecords.stream()
                .map(attendance -> new AttendanceResponse(attendance.getDate(), attendance.getStatus()))
                .collect(Collectors.toList());
    }
}
