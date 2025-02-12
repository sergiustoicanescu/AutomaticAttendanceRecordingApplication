package com.example.dto.Course;

import com.example.dto.Location.LocationDTO;
import com.example.dto.Professor.ProfessorMapper;
import com.example.entity.CourseEntity;

import java.util.ArrayList;
import java.util.List;

public class CourseMapper {

    public static CourseDTO toDTO(CourseEntity course) {
        return new CourseDTO(
                course.getId(),
                course.getName(),
                course.getNumberOfWeeks(),
                course.getLocation() != null ? new LocationDTO(course.getLocation()) : null,
                course.getCode(),
                ProfessorMapper.toDTO(course.getProfessor())
        );
    }

    public static List<CourseDTO> toDTO(List<CourseEntity> courses) {
        List<CourseDTO> courseDTOS = new ArrayList<>();
        for(CourseEntity course : courses) {
            CourseDTO courseDTO = CourseMapper.toDTO(course);
            courseDTOS.add(courseDTO);
        }
        return courseDTOS;
    }

}
