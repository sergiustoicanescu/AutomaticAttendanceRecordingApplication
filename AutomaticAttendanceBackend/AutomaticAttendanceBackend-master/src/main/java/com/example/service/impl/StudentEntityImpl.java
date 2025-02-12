package com.example.service.impl;

import com.example.dto.Student.StudentDTO;
import com.example.entity.SessionEntity;
import com.example.entity.StudentEntity;
import com.example.repository.StudentEntityRepository;
import com.example.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentEntityImpl implements StudentService {
    @Autowired
    StudentEntityRepository studentEntityRepository;

    @Override
    public StudentEntity findByEmail(String email) {
        Optional<StudentEntity> optionalStudent = studentEntityRepository.findByEmail(email);
        if(optionalStudent.isPresent()) {
            return optionalStudent.get();
        } else {
            throw new RuntimeException("No student found with that email!");
        }
    }

    @Override
    public void save(StudentEntity student) {
        studentEntityRepository.save(student);
    }

    @Override
    public List<StudentDTO> findStudentDTOsBySessionAndCode(SessionEntity session, String code) {
        return studentEntityRepository.findStudentDTOsBySession(session, code);
    }

    @Override
    public List<StudentEntity> findStudentsByCourseCodeAndSessionThatAreNotMarked(String code, SessionEntity session) {
        return studentEntityRepository.findStudentsByCourseCodeAndSessionThatAreNotMarked(session, code);
    }

    @Override
    public StudentEntity findByUserID(Long userId) {
        Optional<StudentEntity> optionalStudent = studentEntityRepository.findByUserID(userId);
        if(optionalStudent.isPresent()) {
            return optionalStudent.get();
        } else {
            throw new RuntimeException("Student could not be found with this user id");
        }
    }

    @Override
    public Optional<StudentEntity> findByEmailOptional(String email) {
        return studentEntityRepository.findByEmail(email);
    }
}
