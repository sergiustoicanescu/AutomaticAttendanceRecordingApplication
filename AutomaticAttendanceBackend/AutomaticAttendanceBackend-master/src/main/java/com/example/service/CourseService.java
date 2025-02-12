package com.example.service;

import com.example.dto.Course.CourseDTO;
import com.example.dto.Location.LocationDTO;
import com.example.entity.CourseEntity;
import com.example.entity.UserEntity;

import java.util.Optional;

public interface CourseService {

    CourseEntity createCourse(CourseDTO courseDto);

    void deleteCourse(String courseId);

    CourseEntity updateCourseName(String courseId, String name);

    CourseEntity updateLocation(Long courseId, LocationDTO locationDTO);

    CourseEntity getCourseByCode(String courseId);

    void updateCourseDetails(String code, CourseDTO courseDTO);

    Optional<Object> findByCodeAndUser(String code, UserEntity user);
}
