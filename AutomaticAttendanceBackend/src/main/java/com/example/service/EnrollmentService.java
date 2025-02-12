package com.example.service;

import com.example.entity.CourseEnrollmentEntity;
import com.example.entity.CourseEntity;
import com.example.entity.StudentEntity;

import java.util.List;

public interface EnrollmentService {
    void save(CourseEnrollmentEntity courseEnrollmentEntity);

    void enrollStudentsInCourse(List<String> studentEmails, CourseEntity course);

    List<StudentEntity> getStudentsByCourseCode(String code);

    void removeStudentFromCourse(CourseEntity course, StudentEntity studentEntity);

    List<StudentEntity> addNewStudentsToCourse(String students, CourseEntity course);

    CourseEnrollmentEntity findEnrollmentByCourseAndStudent(CourseEntity course, StudentEntity student);

    List<CourseEntity> findCoursesByStudent(StudentEntity student);

    String editStudentFromCourse(CourseEntity course, StudentEntity oldStudent, String newEmail);
}
