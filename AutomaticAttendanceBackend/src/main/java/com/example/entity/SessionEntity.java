package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name="Session")
@Data
public class SessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name="course_id", nullable=false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CourseEntity course;

    @Enumerated(EnumType.STRING)
    private SessionType type;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    private int week;

    private boolean active;

    private Integer code;

    private int frequency;
}
