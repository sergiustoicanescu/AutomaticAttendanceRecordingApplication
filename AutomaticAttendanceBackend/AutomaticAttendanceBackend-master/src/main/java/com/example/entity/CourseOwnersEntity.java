package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "CourseOwners", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"course_id", "professor_id"}, name = "UQ_course_professor")
})
@Data
public class CourseOwnersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CourseEntity course;

    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ProfessorEntity professor;
}
