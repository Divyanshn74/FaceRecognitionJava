package com.example.facerecog.dto;

public class RegisterRequest {
    private String rollNo;
    private String name;
    private String fullName;
    private String enrollmentNumber;
    private String imageBase64;

    public RegisterRequest() {
    }

    public RegisterRequest(String rollNo, String name, String fullName, String enrollmentNumber, String imageBase64) {
        this.rollNo = rollNo;
        this.name = name;
        this.fullName = fullName;
        this.enrollmentNumber = enrollmentNumber;
        this.imageBase64 = imageBase64;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEnrollmentNumber() {
        return enrollmentNumber;
    }

    public void setEnrollmentNumber(String enrollmentNumber) {
        this.enrollmentNumber = enrollmentNumber;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}