package com.example.repository;

import com.example.dto.Attendance.AttendanceStatisticsDTO;
import com.example.entity.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AttendanceEntityRepository extends JpaRepository<AttendanceEntity, Long> {
    @Query("SELECT ae FROM AttendanceEntity ae WHERE ae.student = :student and ae.session = :session")
    Optional<AttendanceEntity> findBySessionAndStudent(@Param("session") SessionEntity session, @Param("student") StudentEntity student);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ae FROM AttendanceEntity ae WHERE ae.student = :student and ae.session = :session")
    Optional<AttendanceEntity> findBySessionAndStudentForUpdate(@Param("session") SessionEntity session, @Param("student") StudentEntity student);

    @Query("SELECT new com.example.dto.Attendance.AttendanceStatisticsDTO(" +
            "COUNT(ae), " +
            "SUM(CASE WHEN ae.attendanceStatus = 'PRESENT' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN ae.attendanceStatus = 'ABSENT' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN ae.attendanceStatus = 'AUTHORIZED_ABSENT' THEN 1 ELSE 0 END)) " +
            "FROM AttendanceEntity ae WHERE ae.session = :session")
    AttendanceStatisticsDTO getAttendanceStatisticsBySession(@Param("session") SessionEntity session);

    @Query("SELECT ae FROM AttendanceEntity ae WHERE ae.student = :student AND ae.session.course = :course AND ae.session.week = :week and ae.session.type = :type and ae.attendanceStatus = :status")
    List<AttendanceEntity> findAllPresentAttendancesForStudentInAWeek(@Param("course") CourseEntity course, @Param("student") StudentEntity student, @Param("week") int week, @Param("type") SessionType type, @Param("status") AttendanceStatus status);

    @Query("SELECT COUNT(ae) FROM AttendanceEntity ae WHERE ae.attendanceStatus = 'PRESENT' AND ae.session.course = :course AND ae.session.type = :type AND ae.student = :student")
    Long countPresentByStudentTypeAndCourse(@Param("student") StudentEntity student,@Param("type") SessionType type,@Param("course") CourseEntity course);

    @Query("SELECT ae FROM AttendanceEntity ae WHERE ae.student = :student AND ae.session.course = :course")
    List<AttendanceEntity> findByStudentAndCourse(@Param("student") StudentEntity studentEntity,@Param("course") CourseEntity course);
}
