package com.example.service.impl;

import com.example.dto.Course.CourseDTO;
import com.example.dto.Location.LocationDTO;
import com.example.entity.CourseEntity;
import com.example.entity.ProfessorEntity;
import com.example.entity.UserEntity;
import com.example.generator.CodeGenerator;
import com.example.generator.StudentEmailGenerator;
import com.example.repository.CourseEntityRepository;
import com.example.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourseEntityImpl implements CourseService {
    @Autowired
    CourseEntityRepository courseEntityRepository;

    @Autowired
    ProfessorService professorService;

    @Autowired
    SessionService sessionService;
    
    @Autowired
    EnrollmentService enrollmentService;

    @Autowired
    CourseOwnersService courseOwnersService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseEntity createCourse(CourseDTO courseDto) {
        List<String> studentEmails = StudentEmailGenerator.extractEmails(courseDto.getStudents());

        CourseEntity course = new CourseEntity();
        course.setName(courseDto.getName());
        Optional<ProfessorEntity> professor = professorService.findByUserId(courseDto.getUserId());
        if(professor.isEmpty()){
            throw new RuntimeException("No professor found!");
        }
        course.setProfessor(professor.get());
        course.setCode(generateCode());
        course.setNumberOfWeeks(courseDto.getNumberOfWeeks());

        courseEntityRepository.save(course);

        courseOwnersService.saveOwner(course, professor.get());
        sessionService.createSessions(course, courseDto.getNumberOfWeeks(), courseDto.getSessions());
        enrollmentService.enrollStudentsInCourse(studentEmails, course);

        return course;
    }

    @Override
    public void deleteCourse(String courseId) {
        Optional<CourseEntity> courseEntity = courseEntityRepository.getCourseByCode(courseId);
        if(courseEntity.isPresent()) {
            courseEntityRepository.delete(courseEntity.get());
        } else {
            throw new RuntimeException("No course found with that id");
        }
    }

    @Override
    public CourseEntity updateCourseName(String courseId, String name) {
        CourseEntity course = courseEntityRepository.getCourseByCode(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
        course.setName(name);
        return courseEntityRepository.save(course);
    }

    @Override
    public CourseEntity updateLocation(Long courseId, LocationDTO locationDTO) {
        String location = null;
        if (locationDTO != null) {
            location = locationDTO.getLat() + " " + locationDTO.getLng();
        }
        CourseEntity course = courseEntityRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
        course.setLocation(location);
        return courseEntityRepository.save(course);
    }

    @Override
    public CourseEntity getCourseByCode(String courseId) {
        Optional<CourseEntity> optionalCourse = courseEntityRepository.getCourseByCode(courseId);
        if(optionalCourse.isPresent()) {
            return optionalCourse.get();
        } else {
            throw new RuntimeException("Course could not be found with this code: " + courseId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCourseDetails(String code, CourseDTO courseDTO) {
        CourseEntity course = getCourseByCode(code);
        sessionService.updateNumberOfSessions(course.getCode(), course.getNumberOfWeeks(), courseDTO.getNumberOfWeeks());
        course.setName(courseDTO.getName());
        course.setNumberOfWeeks(courseDTO.getNumberOfWeeks());
        courseEntityRepository.save(course);
    }

    @Override
    public Optional<Object> findByCodeAndUser(String code, UserEntity user) {
        return courseEntityRepository.findByCodeAndUser(code, user);
    }

    private String generateCode() {
        List<String> existingCodes = courseEntityRepository.getCodes();
        String code;
        do {
            code = CodeGenerator.generateCourseCode();
        } while (existingCodes.contains(code));

        return code;
    }
}
