package com.example.dto.Student;

import com.example.entity.StudentEntity;

import java.util.ArrayList;
import java.util.List;

public class StudentMapper {

    public static StudentDTO toDto(StudentEntity student) {
        return new StudentDTO(
                student.getEmail(),
                student.getFirstName(),
                student.getLastName()
        );
    }

    public static List<StudentDTO> toDto (List<StudentEntity> students) {
        List<StudentDTO> studentDTOS = new ArrayList<>();
        for(StudentEntity student : students) {
            StudentDTO studentDTO = toDto(student);
            studentDTOS.add(studentDTO);
        }
        return studentDTOS;
    }

}
