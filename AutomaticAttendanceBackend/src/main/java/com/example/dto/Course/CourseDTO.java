package com.example.dto.Course;

import com.example.dto.Location.LocationDTO;
import com.example.dto.Professor.ProfessorDTO;
import com.example.dto.Session.SessionDTO;

import java.util.List;

public class CourseDTO {
    private Long id;
    private String name;
    private int numberOfWeeks;
    private LocationDTO location;
    private String code;
    private List<SessionDTO> sessions;
    private String students;
    private Integer currentWeek;
    private ProfessorDTO professor;
    private Long userId;

    public CourseDTO(Long id, String name, int numberOfWeeks, LocationDTO location, String code, ProfessorDTO professor) {
        this.id = id;
        this.name = name;
        this.numberOfWeeks = numberOfWeeks;
        this.location = location;
        this.code = code;
        this.professor = professor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfWeeks() {
        return numberOfWeeks;
    }

    public void setNumberOfWeeks(int numberOfWeeks) {
        this.numberOfWeeks = numberOfWeeks;
    }

    public List<SessionDTO> getSessions() {
        return sessions;
    }

    public void setSessions(List<SessionDTO> sessions) {
        this.sessions = sessions;
    }

    public String getStudents() {
        return students;
    }

    public void setStudents(String students) {
        this.students = students;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getCurrentWeek() {
        return currentWeek;
    }

    public void setCurrentWeek(Integer currentWeek) {
        this.currentWeek = currentWeek;
    }

    public ProfessorDTO getProfessor() {
        return professor;
    }

    public void setProfessor(ProfessorDTO professor) {
        this.professor = professor;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
