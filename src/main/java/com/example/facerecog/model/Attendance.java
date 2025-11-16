package com.example.facerecog.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String status;

    public Attendance() {
        this.status = "PRESENT"; // Default value
    }

    public Attendance(Long id, Student student, LocalDate date, String status) {
        this.id = id;
        this.student = student;
        this.date = date;
        this.status = status;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Student student;
        private LocalDate date;
        private String status = "PRESENT"; // Default value

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder student(Student student) {
            this.student = student;
            return this;
        }

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Attendance build() {
            return new Attendance(id, student, date, status);
        }
    }
}
