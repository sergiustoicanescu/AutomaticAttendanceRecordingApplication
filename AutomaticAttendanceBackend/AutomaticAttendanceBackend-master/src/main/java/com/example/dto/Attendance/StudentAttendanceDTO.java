package com.example.dto.Attendance;

import com.example.entity.SessionType;

public class StudentAttendanceDTO {
    private SessionType type;

    private Long present;

    public SessionType getType() {
        return type;
    }

    public void setType(SessionType type) {
        this.type = type;
    }

    public Long getPresent() {
        return present;
    }

    public void setPresent(Long present) {
        this.present = present;
    }
}
