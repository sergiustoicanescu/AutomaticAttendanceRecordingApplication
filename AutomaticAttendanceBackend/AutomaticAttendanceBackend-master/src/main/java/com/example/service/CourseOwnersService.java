package com.example.service;

import com.example.entity.CourseEntity;
import com.example.entity.ProfessorEntity;
import com.example.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface CourseOwnersService {
    List<CourseEntity> getCoursesByUserId(Long userId);

    List<ProfessorEntity> getProfessorsByCourseCode(String code);

    void saveOwner(CourseEntity course, ProfessorEntity professorEntity);

    Optional<Object> findByCourseAndUser(CourseEntity courseEntity, UserEntity user);

    void removeOwner(CourseEntity course, ProfessorEntity professor);

    void addCourseOwner(CourseEntity course, ProfessorEntity professor);
}
