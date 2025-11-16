package com.example.facerecog.service;

import com.example.facerecog.model.Student;
import com.example.facerecog.repository.StudentRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Transactional
    public void updateStudentAndFace(Student student, String newFaceImageBase64) {
        if (newFaceImageBase64 != null && !newFaceImageBase64.isBlank()) {
            System.out.println("Updating face embedding for student ID: " + student.getId());
            String newFaceEmbedding = faceEngineService.getEmbedding(newFaceImageBase64);
            if (newFaceEmbedding != null) {
                student.setFaceEmbedding(newFaceEmbedding);
            } else {
                System.err.println("Could not generate new face embedding. Face will not be updated.");
            }
        }
        studentRepository.save(student);
    }

    @Transactional
    public void importStudentsFromCsv(MultipartFile file) throws IOException, CsvValidationException {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] line;
            // Skip header row
            reader.readNext();

            List<Student> studentsToSave = new ArrayList<>();
            while ((line = reader.readNext()) != null) {
                Student student = new Student();
                student.setName(line[0]);
                student.setFullName(line[1]);
                student.setRollNo(line[2]);
                student.setEnrollmentNumber(line[3]);
                // faceEmbedding is left null
                studentsToSave.add(student);
            }
            studentRepository.saveAll(studentsToSave);
        }
    }
}
