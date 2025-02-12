package com.example.repository;

import com.example.entity.CourseEnrollmentEntity;
import com.example.entity.CourseEntity;
import com.example.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseEnrollmentEntityRepository extends JpaRepository<CourseEnrollmentEntity, Long> {
    @Query("SELECT ce.student FROM CourseEnrollmentEntity ce WHERE ce.course.code = :code ORDER BY ce.student.email ASC")
    List<StudentEntity> getStudentsByCourseCode(@Param("code") String code);

    @Query("SELECT ce FROM CourseEnrollmentEntity ce WHERE ce.course = :course and ce.student = :student")
    CourseEnrollmentEntity findByCourseAndStudent(@Param("course") CourseEntity course,@Param("student") StudentEntity studentEntity);

    @Query("SELECT ce.student FROM CourseEnrollmentEntity ce WHERE ce.course = :course and ce.student.email in :emails")
    List<StudentEntity> getStudentsFromListOfEmails(@Param("course") CourseEntity course,@Param("emails") List<String> studentEmails);

    @Query("SELECT ce.course FROM CourseEnrollmentEntity ce where ce.student = :student")
    List<CourseEntity> findCoursesByStudent(@Param("student") StudentEntity student);
    @Query("SELECT ce.student FROM CourseEnrollmentEntity ce where ce.course = :course and ce.student.email = :email")
    Optional<StudentEntity> findStudentByCourseAndEmail(@Param("course") CourseEntity course,@Param("email") String newEmail);
}
