package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name="Attendance", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"course_session_id", "student_id"}, name = "UQ_session_student")
})
@Data
public class AttendanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_session_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SessionEntity session;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private StudentEntity student;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus attendanceStatus;

    private LocalDate date;

    private LocalTime time;

    @Column(length = 255)
    private String comment;

    public void setComment(String comment) {
        final int MAX_COMMENT_LENGTH = 255;
        if (comment != null && comment.length() > MAX_COMMENT_LENGTH) {
            this.comment = comment.substring(0, MAX_COMMENT_LENGTH);
        } else {
            this.comment = comment;
        }
    }

}
