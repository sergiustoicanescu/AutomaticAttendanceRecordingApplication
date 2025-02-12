package com.example.service.impl;

import com.example.entity.CourseEntity;
import com.example.entity.CourseOwnersEntity;
import com.example.entity.ProfessorEntity;
import com.example.entity.UserEntity;
import com.example.error.ValidationErrorException;
import com.example.repository.CourseOwnersEntityRepository;
import com.example.service.CourseOwnersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourseOwnersEntityImpl implements CourseOwnersService {

    @Autowired
    CourseOwnersEntityRepository courseOwnersEntityRepository;

    @Override
    public List<CourseEntity> getCoursesByUserId(Long userId) {
        List<CourseEntity> courses = courseOwnersEntityRepository.getCoursesByUserId(userId);
        if (courses.isEmpty()) {
            throw new ValidationErrorException("No courses found!");
        }
        return courses;
    }

    @Override
    public List<ProfessorEntity> getProfessorsByCourseCode(String code) {
        return courseOwnersEntityRepository.getProfessorsByCourseCode(code);
    }

    @Override
    public void saveOwner(CourseEntity course, ProfessorEntity professorEntity) {
        CourseOwnersEntity courseOwnersEntity = new CourseOwnersEntity();
        courseOwnersEntity.setCourse(course);
        courseOwnersEntity.setProfessor(professorEntity);
        courseOwnersEntityRepository.save(courseOwnersEntity);
    }

    @Override
    public Optional<Object> findByCourseAndUser(CourseEntity courseEntity, UserEntity user) {
        return courseOwnersEntityRepository.findByCourseAndUser(courseEntity, user);
    }

    public CourseOwnersEntity findByCourseAndProfessor(CourseEntity course, ProfessorEntity professor) {
        Optional<CourseOwnersEntity> courseOwners = courseOwnersEntityRepository.findByCourseAndProfessor(course, professor);
        if(courseOwners.isPresent()) {
            return courseOwners.get();
        } else {
            throw new RuntimeException("No course owner entity found!");
        }
    }

    @Override
    @Transactional
    public void removeOwner(CourseEntity course, ProfessorEntity professor) {
        CourseOwnersEntity courseOwners = findByCourseAndProfessor(course, professor);
        courseOwnersEntityRepository.delete(courseOwners);
    }

    @Override
    public void addCourseOwner(CourseEntity course, ProfessorEntity professor) {
        Optional<CourseOwnersEntity> courseOwnersEntity = courseOwnersEntityRepository.findByCourseAndProfessor(course, professor);
        if(courseOwnersEntity.isPresent()) {
            throw new RuntimeException("The professor is already in the course!");
        }
        CourseOwnersEntity courseOwner = new CourseOwnersEntity();
        courseOwner.setProfessor(professor);
        courseOwner.setCourse(course);
        courseOwnersEntityRepository.save(courseOwner);
    }
}
