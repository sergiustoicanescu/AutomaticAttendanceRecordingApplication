package com.example.dto.Session;

import com.example.dto.Course.CourseDTO;
import com.example.dto.Student.StudentDTO;

import java.util.List;

public class SessionDetailsDTO {
    private CourseDTO course;
    private SessionDTO session;
    private List<StudentDTO> students;

    public SessionDetailsDTO(CourseDTO course, SessionDTO session, List<StudentDTO> students) {
        this.course = course;
        this.session = session;
        this.students = students;
    }

    public SessionDetailsDTO(CourseDTO course, SessionDTO session) {
        this.course = course;
        this.session = session;
    }

    public SessionDTO getSession() {
        return session;
    }

    public List<StudentDTO> getStudents() {
        return students;
    }

    public void setStudents(List<StudentDTO> students) {
        this.students = students;
    }

    public void setSession(SessionDTO session) {
        this.session = session;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }
}
