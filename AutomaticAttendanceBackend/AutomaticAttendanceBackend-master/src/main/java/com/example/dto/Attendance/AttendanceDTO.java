package com.example.dto.Attendance;

import com.example.entity.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceDTO {
    private AttendanceStatus status;
    private LocalDate date;
    private LocalTime time;

    public AttendanceDTO(AttendanceStatus status, LocalDate date, LocalTime time, String comment) {
        this.status = status;
        this.date = date;
        this.time = time;
        this.comment = comment;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private String comment;
}
