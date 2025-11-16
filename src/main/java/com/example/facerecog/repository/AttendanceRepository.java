package com.example.facerecog.repository;

import com.example.facerecog.model.Attendance;
import com.example.facerecog.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByStudentAndDate(Student student, LocalDate date);
    List<Attendance> findByStudent(Student student);
    List<Attendance> findByDate(LocalDate date);
    @Query("SELECT a FROM Attendance a WHERE a.date = :date AND a.student.id IN :studentIds")
    List<Attendance> findByDateAndStudentIdIn(@Param("date") LocalDate date, @Param("studentIds") List<Long> studentIds);

    @Query("SELECT a FROM Attendance a JOIN FETCH a.student s " +
           "WHERE (:enrollmentNumber IS NULL OR s.enrollmentNumber = :enrollmentNumber) " +
           "AND (:startDate IS NULL OR a.date >= :startDate) " +
           "AND (:endDate IS NULL OR a.date <= :endDate)")
    List<Attendance> search(@Param("enrollmentNumber") String enrollmentNumber,
                            @Param("startDate") LocalDate startDate,
                            @Param("endDate") LocalDate endDate);
}
