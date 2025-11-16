package com.example.facerecog.dto;

public class StudentAttendanceDTO {
    private Long studentId;
    private String name;
    private String rollNo;
    private String status; // "PRESENT", "ABSENT", or null

    public StudentAttendanceDTO() {
        // Default constructor needed for Spring form binding
    }

    public StudentAttendanceDTO(Long studentId, String name, String rollNo, String status) {
        this.studentId = studentId;
        this.name = name;
        this.rollNo = rollNo;
        this.status = status;
    }

    // Getters and Setters
    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
