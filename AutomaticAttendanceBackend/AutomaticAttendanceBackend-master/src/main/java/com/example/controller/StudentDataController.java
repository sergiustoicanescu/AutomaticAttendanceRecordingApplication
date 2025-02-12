package com.example.controller;

import com.example.dto.Attendance.StudentAttendanceDTO;
import com.example.dto.Course.CourseMapper;
import com.example.dto.Location.UserLocationDTO;
import com.example.dto.Session.SessionDetailsDTO;
import com.example.dto.Session.SessionMapper;
import com.example.entity.CourseEntity;
import com.example.entity.SessionEntity;
import com.example.entity.SessionType;
import com.example.entity.StudentEntity;
import com.example.error.ValidationErrorException;
import com.example.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentDataController {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private CourseService courseService;

    @GetMapping("/attendance/session/{userId}/{courseCode}/{sessionCode}")
    @PreAuthorize("@customSecurityService.canUserAccessByUserID(#userId, authentication)")
    public ResponseEntity<?> getAttendanceStatistics(@PathVariable Long userId, @PathVariable String courseCode, @PathVariable String sessionCode) {
        SessionEntity session = sessionService.findByCourseCodeAndSessionCode(courseCode, sessionCode);
        CourseEntity course = session.getCourse();
        StudentEntity student = studentService.findByUserID(userId);

        if(enrollmentService.findEnrollmentByCourseAndStudent(course, student) == null) {
            throw new ValidationErrorException("The student is not enrolled in this course!");
        }

        if(!attendanceService.verifyAttendance(session, student)) {
            throw new ValidationErrorException("The student is already present in a session with this type this week!");
        }

        return new ResponseEntity<>(new SessionDetailsDTO(CourseMapper.toDTO(course), SessionMapper.toDTO(session)), HttpStatus.OK);
    }

    @PostMapping("/attendance/markAttendance/{userId}/{sessionId}/{sessionCode}")
    @PreAuthorize("@customSecurityService.canUserAccessByUserID(#userId, authentication)")
    @Transactional
    public ResponseEntity<CourseEntity> markAttendance(@PathVariable Long userId, @PathVariable Long sessionId, @PathVariable String sessionCode, @RequestBody UserLocationDTO userLocationDTO) {
        SessionEntity session = sessionService.findByIdAndCode(sessionId, sessionCode);
        StudentEntity student = studentService.findByEmail(userLocationDTO.getEmail());
        attendanceService.markAttendanceForStudent(session, student, userLocationDTO.getUserLocation());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/statistics/courses/{userId}")
    @PreAuthorize("@customSecurityService.canUserAccessByUserID(#userId, authentication)")
    public ResponseEntity<?> getCoursesByStudent(@PathVariable Long userId) {
        StudentEntity student = studentService.findByUserID(userId);

        List<CourseEntity> courses = enrollmentService.findCoursesByStudent(student);

        return new ResponseEntity<>(CourseMapper.toDTO(courses), HttpStatus.OK);
    }

    @GetMapping("/statistics/attendances/{userId}/{code}")
    @PreAuthorize("@customSecurityService.canUserAccessByUserID(#userId, authentication)")
    public ResponseEntity<?> getCoursesByStudent(@PathVariable Long userId, @PathVariable String code) {
        StudentEntity student = studentService.findByUserID(userId);
        CourseEntity course = courseService.getCourseByCode(code);

        List<StudentAttendanceDTO> studentAttendanceDTOS = new ArrayList<>();
        List<SessionType> types = sessionService.findAllTypesFromACourse(course);
        for(SessionType type : types) {
            StudentAttendanceDTO studentAttendanceDTO = new StudentAttendanceDTO();
            studentAttendanceDTO.setType(type);
            studentAttendanceDTO.setPresent(attendanceService.countPresentByStudentTypeAndCourse(student, type, course));
            studentAttendanceDTOS.add(studentAttendanceDTO);
        }

        return new ResponseEntity<>(studentAttendanceDTOS, HttpStatus.OK);
    }
}
