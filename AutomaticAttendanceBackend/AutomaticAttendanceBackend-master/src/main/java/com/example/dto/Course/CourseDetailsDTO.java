package com.example.dto.Course;

import com.example.dto.Professor.ProfessorDTO;
import com.example.dto.Session.SessionDTO;
import com.example.dto.Student.StudentDTO;

import java.util.List;

public class CourseDetailsDTO {
    private CourseDTO course;
    private List<SessionDTO> sessions;

    private List<StudentDTO> students;

    private List<WeekDTO> weeks;

    private List<ProfessorDTO> professors;

    public CourseDetailsDTO(CourseDTO courseDTO, List<SessionDTO> sessionDTOS) {
        this.course = courseDTO;
        this.sessions = sessionDTOS;
    }

    public CourseDetailsDTO(CourseDTO courseDTO, List<SessionDTO> sessionDTOS, List<StudentDTO> studentDTOS, List<WeekDTO> weeks) {
        this.course = courseDTO;
        this.sessions = sessionDTOS;
        this.students = studentDTOS;
        this.weeks = weeks;
    }

    public CourseDetailsDTO(CourseDTO course, List<SessionDTO> sessions, List<StudentDTO> students, List<WeekDTO> weeks, List<ProfessorDTO> professors) {
        this.course = course;
        this.sessions = sessions;
        this.students = students;
        this.weeks = weeks;
        this.professors = professors;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

    public List<SessionDTO> getSessions() {
        return sessions;
    }

    public void setSessions(List<SessionDTO> sessions) {
        this.sessions = sessions;
    }

    public List<StudentDTO> getStudents() {
        return students;
    }

    public void setStudents(List<StudentDTO> students) {
        this.students = students;
    }

    public List<WeekDTO> getWeeks() {
        return weeks;
    }

    public void setWeeks(List<WeekDTO> weeks) {
        this.weeks = weeks;
    }

    public List<ProfessorDTO> getProfessors() {
        return professors;
    }

    public void setProfessors(List<ProfessorDTO> professors) {
        this.professors = professors;
    }
}
