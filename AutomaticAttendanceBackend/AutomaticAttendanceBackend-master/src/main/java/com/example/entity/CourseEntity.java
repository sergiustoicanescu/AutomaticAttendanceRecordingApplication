package com.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name="Course")
@Data
public class CourseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="professor_id", nullable=false)
    private ProfessorEntity professor;

    private String name;

    private int numberOfWeeks;

    private String location;

    @NotNull
    @Column(unique = true)
    private String code;
}
