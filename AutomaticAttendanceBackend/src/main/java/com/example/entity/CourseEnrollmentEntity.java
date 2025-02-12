package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name="Enrollment", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"course_id", "student_id"}, name = "UQ_course_student")
})
@Data
public class CourseEnrollmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CourseEntity course;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private StudentEntity student;

}