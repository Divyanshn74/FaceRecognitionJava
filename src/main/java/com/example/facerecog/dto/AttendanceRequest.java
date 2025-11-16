package com.example.facerecog.dto;

public class AttendanceRequest {
    private String rollNo;
    private String imageBase64;

    public AttendanceRequest() {
    }

    public AttendanceRequest(String rollNo, String imageBase64) {
        this.rollNo = rollNo;
        this.imageBase64 = imageBase64;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}