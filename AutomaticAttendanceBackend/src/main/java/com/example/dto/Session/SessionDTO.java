package com.example.dto.Session;

import com.example.entity.SessionType;

import java.time.LocalDate;
import java.time.LocalTime;

public class SessionDTO {
    private Long id;
    private String name;
    private SessionType type;
    private LocalDate startDate;
    private LocalTime startTime;
    private boolean active;
    private int week;
    private int frequency;
    private LocalTime endTime;
    private Long courseId;
    private Integer code;

    public SessionDTO() {}

    public SessionDTO(Long id, String name, SessionType type, LocalDate startDate, LocalTime startTime, boolean active, int week, int frequency, LocalTime endTime, Long courseId, Integer code) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.startDate = startDate;
        this.startTime = startTime;
        this.active = active;
        this.week = week;
        this.frequency = frequency;
        this.endTime = endTime;
        this.courseId = courseId;
        this.code = code;
    }

    public SessionDTO(String name, SessionType sessionType, int frequency) {
        this.name = name;
        this.type = sessionType;
        this.frequency = frequency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SessionType getType() {
        return type;
    }

    public void setType(String type) {
        this.type = SessionType.valueOf(type);
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public void setType(SessionType type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    public boolean isActive() {
        return active;
    }
    public Long getCourseId() {
        return courseId;
    }
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
