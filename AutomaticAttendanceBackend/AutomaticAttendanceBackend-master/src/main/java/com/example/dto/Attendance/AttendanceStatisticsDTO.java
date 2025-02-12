package com.example.dto.Attendance;

public class AttendanceStatisticsDTO {
    private Long total;
    private Long present;
    private Long absent;
    private Long authorized;

    public AttendanceStatisticsDTO(Long total, Long present, Long absent, Long authorized) {
        this.total = total;
        this.present = present;
        this.absent = absent;
        this.authorized = authorized;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getPresent() {
        return present;
    }

    public void setPresent(Long present) {
        this.present = present;
    }

    public Long getAbsent() {
        return absent;
    }

    public void setAbsent(Long absent) {
        this.absent = absent;
    }

    public Long getAuthorized() {
        return authorized;
    }

    public void setAuthorized(Long authorized) {
        this.authorized = authorized;
    }
}
