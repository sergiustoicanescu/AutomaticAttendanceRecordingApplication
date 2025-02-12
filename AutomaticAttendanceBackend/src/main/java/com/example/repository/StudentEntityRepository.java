package com.example.repository;

import com.example.dto.Student.StudentDTO;
import com.example.entity.SessionEntity;
import com.example.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentEntityRepository extends JpaRepository<StudentEntity, Long> {
    Optional<StudentEntity> findByEmail(String email);

    @Query("SELECT new com.example.dto.Student.StudentDTO(s.firstName, s.lastName, s.email, ae.attendanceStatus, ae.date, ae.time, ae.comment) FROM StudentEntity s JOIN CourseEnrollmentEntity ce ON ce.student = s LEFT JOIN AttendanceEntity ae ON ae.student = s AND ae.session = :session WHERE ce.course.code = :code ORDER BY s.email")
    List<StudentDTO> findStudentDTOsBySession(@Param("session") SessionEntity session, @Param("code") String code);

    @Query("SELECT s FROM StudentEntity s JOIN CourseEnrollmentEntity ce ON ce.student = s LEFT JOIN AttendanceEntity ae ON ae.student = s AND ae.session = :session WHERE ce.course.code = :code AND ae.attendanceStatus is null ORDER BY s.email")
    List<StudentEntity> findStudentsByCourseCodeAndSessionThatAreNotMarked(@Param("session") SessionEntity session, @Param("code") String code);

    @Query("SELECT s FROM StudentEntity s WHERE s.userEntity.id = :userId")
    Optional<StudentEntity> findByUserID(@Param("userId") Long userId);
}
