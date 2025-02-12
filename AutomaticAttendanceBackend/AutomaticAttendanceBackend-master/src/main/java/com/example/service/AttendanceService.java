package com.example.service;

import com.example.dto.Attendance.AttendanceDTO;
import com.example.dto.Attendance.AttendanceStatisticsDTO;
import com.example.dto.Location.LocationDTO;
import com.example.dto.Student.StudentDTO;
import com.example.entity.*;

import java.util.List;
import java.util.Optional;

public interface AttendanceService {

    StudentDTO markAttendance(SessionEntity session, StudentEntity student, StudentDTO studentDTO);

    AttendanceDTO markBulkAttendance(SessionEntity session, List<StudentEntity> students, AttendanceDTO attendanceDTO);

    void saveComment(SessionEntity session, StudentEntity student, String comment);

    AttendanceStatisticsDTO getAttendanceStatisticsBySession(SessionEntity session);

    boolean verifyLocationProximity(double userLat, double userLng,
                             double courseLat, double courseLng);

    boolean verifyAttendance(SessionEntity session, StudentEntity student);

    void markStudentPresent(SessionEntity session, StudentEntity student);

    Long countPresentByStudentTypeAndCourse(StudentEntity student, SessionType type, CourseEntity course);

    void deleteAllAttendancesForStudentByCourse(CourseEntity course, StudentEntity studentEntity);

    Optional<AttendanceEntity> findByStudentAndSession(StudentEntity student, SessionEntity session);

    void addAttendanceToNewStudentFromOldStudent(CourseEntity course, StudentEntity oldStudent, StudentEntity newStudent);

    void markAttendanceForStudent(SessionEntity session, StudentEntity student, LocationDTO userLocation);
}
