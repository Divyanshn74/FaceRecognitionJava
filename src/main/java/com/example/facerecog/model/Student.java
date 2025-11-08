package com.example.facerecog.model;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String rollNo;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String enrollmentNumber;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String faceEmbedding;

    public Student() {
    }

    public Student(Long id, String rollNo, String name, String fullName, String enrollmentNumber, String faceEmbedding) {
        this.id = id;
        this.rollNo = rollNo;
        this.name = name;
        this.fullName = fullName;
        this.enrollmentNumber = enrollmentNumber;
        this.faceEmbedding = faceEmbedding;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getRollNo() {
        return rollNo;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEnrollmentNumber() {
        return enrollmentNumber;
    }

    public String getFaceEmbedding() {
        return faceEmbedding;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEnrollmentNumber(String enrollmentNumber) {
        this.enrollmentNumber = enrollmentNumber;
    }

    public void setFaceEmbedding(String faceEmbedding) {
        this.faceEmbedding = faceEmbedding;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String rollNo;
        private String name;
        private String fullName;
        private String enrollmentNumber;
        private String faceEmbedding;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder rollNo(String rollNo) {
            this.rollNo = rollNo;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder enrollmentNumber(String enrollmentNumber) {
            this.enrollmentNumber = enrollmentNumber;
            return this;
        }

        public Builder faceEmbedding(String faceEmbedding) {
            this.faceEmbedding = faceEmbedding;
            return this;
        }

        public Student build() {
            return new Student(id, rollNo, name, fullName, enrollmentNumber, faceEmbedding);
        }
    }
}
