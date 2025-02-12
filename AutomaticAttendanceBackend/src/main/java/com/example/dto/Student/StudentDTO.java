package com.example.dto.Student;

import com.example.dto.Attendance.AttendanceDTO;
import com.example.entity.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class StudentDTO {
    private String email;
    private String firstName;
    private String lastName;
    private AttendanceDTO attendance;

    public StudentDTO() {}

    public StudentDTO(String email, String firstName, String lastName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public StudentDTO(String firstName, String lastName, String email, AttendanceStatus status, LocalDate date, LocalTime time, String comment) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.attendance = new AttendanceDTO(status, date, time, comment);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public AttendanceDTO getAttendance() {
        return attendance;
    }

    public void setAttendance(AttendanceDTO attendance) {
        this.attendance = attendance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
