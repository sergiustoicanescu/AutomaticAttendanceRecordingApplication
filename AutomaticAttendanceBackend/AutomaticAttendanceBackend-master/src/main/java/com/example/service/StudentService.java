package com.example.service;

import com.example.dto.Student.StudentDTO;
import com.example.entity.SessionEntity;
import com.example.entity.StudentEntity;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    StudentEntity findByEmail(String email);
    void save(StudentEntity student);

    List<StudentDTO> findStudentDTOsBySessionAndCode(SessionEntity session, String code);

    List<StudentEntity> findStudentsByCourseCodeAndSessionThatAreNotMarked(String code, SessionEntity session);

    StudentEntity findByUserID(Long userId);

    Optional<StudentEntity> findByEmailOptional(String email);
}
