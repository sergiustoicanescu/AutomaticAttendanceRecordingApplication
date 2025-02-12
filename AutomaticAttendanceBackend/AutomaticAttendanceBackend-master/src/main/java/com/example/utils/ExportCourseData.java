package com.example.utils;

import com.example.entity.*;
import com.example.service.AttendanceService;
import com.example.service.EnrollmentService;
import com.example.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExportCourseData {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private AttendanceService attendanceService;

    public String getExportColumns(CourseEntity course) {
        StringBuilder columns = new StringBuilder("First Name, Last Name, Email, ");
        List<SessionType> sessionTypeList = sessionService.findAllTypesFromACourse(course);
        for(SessionType sessionType : sessionTypeList) {
            columns.append("Total ").append(sessionType.name()).append(", ");
        }
        List<SessionEntity> sessionEntities = sessionService.getSessionsByCourseCode(course.getCode());
        Map<Integer, List<SessionEntity>> sessionsGroupedByWeek = sessionEntities.stream()
                .collect(Collectors.groupingBy(SessionEntity::getWeek));
        for(Map.Entry<Integer, List<SessionEntity>> entry : sessionsGroupedByWeek.entrySet()) {
            String week = "Week " + entry.getKey();
            for(SessionEntity session : entry.getValue()) {
                columns.append(week).append(" - ").append(session.getName()).append(", ");
            }
        }
        columns.append("\n");

        return columns.toString();
    }

    public String getExportContent(CourseEntity course) {
        List<String> rows = new ArrayList<>();
        List<SessionType> sessionTypeList = sessionService.findAllTypesFromACourse(course);
        List<SessionEntity> sessionEntities = sessionService.getSessionsByCourseCode(course.getCode());
        List<StudentEntity> students = enrollmentService.getStudentsByCourseCode(course.getCode());
        for(StudentEntity student : students) {
            StringBuilder row = new StringBuilder();
            String firstName = student.getFirstName() != null ? student.getFirstName() : " ";
            String lastName = student.getLastName() != null ? student.getLastName() : " ";
            row.append(firstName).append(", ").append(lastName).append(", ").append(student.getEmail()).append(", ");
            for(SessionType sessionType : sessionTypeList) {
                row.append(attendanceService.countPresentByStudentTypeAndCourse(student, sessionType, course)).append(", ");
            }
            for(SessionEntity session : sessionEntities) {
                Optional<AttendanceEntity> attendanceEntityOptional = attendanceService.findByStudentAndSession(student, session);
                if(attendanceEntityOptional.isPresent() && attendanceEntityOptional.get().getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
                    row.append("1, ");
                } else {
                    row.append("0, ");
                }
            }
            rows.add(row.toString());
        }
        return String.join("\n", rows);
    }
}
