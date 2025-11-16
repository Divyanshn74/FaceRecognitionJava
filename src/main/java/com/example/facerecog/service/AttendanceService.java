package com.example.facerecog.service;

import com.example.facerecog.dto.AttendanceResponse;
import com.example.facerecog.dto.StudentAttendanceDTO;
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

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, StudentRepository studentRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public String markAttendance(String studentIdentifier) {
        Optional<Student> studentOptional = studentRepository.findByEnrollmentNumber(studentIdentifier);
        if (studentOptional.isEmpty()) {
            return "Student not found";
        }
        Student student = studentOptional.get();
        LocalDate today = LocalDate.now();

        Optional<Attendance> existingAttendance = attendanceRepository.findByStudentAndDate(student, today);
        if (existingAttendance.isPresent()) {
            return "ALREADY_PRESENT";
        }

        Attendance newAttendance = new Attendance();
        newAttendance.setStudent(student);
        newAttendance.setDate(today);
        newAttendance.setStatus("PRESENT");
        attendanceRepository.save(newAttendance);
        return "PRESENT";
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

    public List<Attendance> searchAttendance(String enrollmentNumber, LocalDate startDate, LocalDate endDate) {
        if (enrollmentNumber != null && enrollmentNumber.isBlank()) {
            enrollmentNumber = null;
        }
        return attendanceRepository.search(enrollmentNumber, startDate, endDate);
    }

    @Transactional
    public void updateAttendanceStatus(Long attendanceId, String status) {
        attendanceRepository.findById(attendanceId).ifPresent(attendance -> {
            attendance.setStatus(status);
            attendanceRepository.save(attendance);
        });
    }

    @Transactional(readOnly = true)
    public List<StudentAttendanceDTO> getStudentAttendanceForDate(LocalDate date) {
        List<Student> allStudents = studentRepository.findAll();
        List<Attendance> attendanceForDate = attendanceRepository.findByDate(date);

        Map<Long, String> attendanceStatusMap = attendanceForDate.stream()
                .collect(Collectors.toMap(att -> att.getStudent().getId(), Attendance::getStatus));

        return allStudents.stream()
                .map(student -> new StudentAttendanceDTO(
                        student.getId(),
                        student.getName(),
                        student.getRollNo(),
                        attendanceStatusMap.get(student.getId())
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveManualAttendance(LocalDate date, List<StudentAttendanceDTO> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return;
        }

        // Filter out any malformed entries from the form submission
        List<StudentAttendanceDTO> validStatuses = statuses.stream()
                .filter(dto -> dto != null && dto.getStudentId() != null)
                .collect(Collectors.toList());

        if (validStatuses.isEmpty()) {
            return;
        }

        List<Long> studentIds = validStatuses.stream().map(StudentAttendanceDTO::getStudentId).collect(Collectors.toList());

        List<Attendance> existingRecords = attendanceRepository.findByDateAndStudentIdIn(date, studentIds);
        Map<Long, Attendance> existingRecordMap = existingRecords.stream()
                .collect(Collectors.toMap(att -> att.getStudent().getId(), att -> att));

        List<Student> allStudents = studentRepository.findAllById(studentIds);
        Map<Long, Student> studentMap = allStudents.stream()
                .collect(Collectors.toMap(Student::getId, student -> student));

        List<Attendance> toSave = validStatuses.stream().map(dto -> {
            Attendance record = existingRecordMap.getOrDefault(dto.getStudentId(), new Attendance());
            Student student = studentMap.get(dto.getStudentId());
            
            // If student is not found in the DB, skip this record
            if (student == null) {
                return null;
            }

            record.setStudent(student);
            record.setDate(date);
            record.setStatus(dto.getStatus() != null ? dto.getStatus() : "ABSENT");
            return record;
        }).filter(java.util.Objects::nonNull).collect(Collectors.toList());

        attendanceRepository.saveAll(toSave);
    }
}
