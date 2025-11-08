package com.example.facerecog.service;

import com.example.facerecog.model.Student;
import com.example.facerecog.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Map;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final FaceEngineService faceEngineService;

    public StudentService(StudentRepository studentRepository, FaceEngineService faceEngineService) {
        this.studentRepository = studentRepository;
        this.faceEngineService = faceEngineService;
    }

    @Transactional
    public Map<String, String> registerStudent(String rollNo, String name, String fullName, String enrollmentNumber, String base64Image) {
        System.out.println("Attempting to register student with rollNo: " + rollNo);

        Optional<Student> existingStudent = studentRepository.findByEnrollmentNumber(enrollmentNumber);
        if (existingStudent.isPresent()) {
            System.out.println("Student with enrollmentNumber " + enrollmentNumber + " already exists.");
            return Map.of("status", "Student already registered");
        }

        System.out.println("Getting face embedding for student " + name);
        String faceEmbedding = faceEngineService.getEmbedding(base64Image);

        if (faceEmbedding == null) {
            System.err.println("Failed to get face embedding for student " + name);
            return Map.of("status", "Failed to get face embedding");
        }

        Student newStudent = com.example.facerecog.model.Student.builder()
                .rollNo(rollNo)
                .name(name)
                .fullName(fullName)
                .enrollmentNumber(enrollmentNumber)
                .faceEmbedding(faceEmbedding)
                .build();

        studentRepository.save(newStudent);
        System.out.println("Student " + name + " registered successfully.");
        return Map.of("status", "Student registered successfully");
    }
}
