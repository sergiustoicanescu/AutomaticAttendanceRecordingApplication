package com.example.controller;


import com.example.dto.Attendance.AttendanceDTO;
import com.example.dto.Attendance.AttendanceStatisticsDTO;
import com.example.dto.Course.*;
import com.example.dto.Professor.ProfessorDTO;
import com.example.dto.Professor.ProfessorMapper;
import com.example.dto.Session.SessionDTO;
import com.example.dto.Session.SessionDetailsDTO;
import com.example.dto.Session.SessionMapper;
import com.example.dto.Student.StudentDTO;
import com.example.dto.Student.StudentMapper;
import com.example.entity.CourseEntity;
import com.example.entity.ProfessorEntity;
import com.example.entity.SessionEntity;
import com.example.entity.StudentEntity;
import com.example.service.*;
import com.example.utils.ExportCourseData;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/professor")
public class ProfessorDataController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private CourseOwnersService courseOwnersService;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private ExportCourseData exportCourseData;

    @GetMapping("/courses/{userId}")
    @PreAuthorize("@customSecurityService.canUserAccessByUserID(#userId, authentication)")
    public ResponseEntity<?> getCoursesByProfessorUserId(@PathVariable Long userId) {
        List<CourseEntity> courses = courseOwnersService.getCoursesByUserId(userId);
        return new ResponseEntity<>(CourseMapper.toDTO(courses), HttpStatus.OK);
    }

    @GetMapping("/courseAndSession/{userId}/{code}")
    @PreAuthorize("@customSecurityService.canUserAccessByCourseCode(#code, authentication)")
    public ResponseEntity<?> getCourseByCode(@PathVariable Long userId, @PathVariable String code) {
        CourseEntity course = courseService.getCourseByCode(code);
        CourseDTO courseDTO = CourseMapper.toDTO(course);
        courseDTO.setUserId(userId);

        List<SessionEntity> sessions = sessionService.getSessionsByCourseCode(code);
        List<SessionDTO> sessionDTOS = SessionMapper.toDTO(sessions);

        courseDTO.setCurrentWeek(sessionService.findCurrentWeek(sessionDTOS));

        CourseDetailsDTO courseDetailsDTO = new CourseDetailsDTO(courseDTO, sessionDTOS);
        return new ResponseEntity<>(courseDetailsDTO, HttpStatus.OK);
    }

    @GetMapping("/allCourseDetails/{userId}/{code}")
    @PreAuthorize("@customSecurityService.canUserAccessByCourseCode(#code, authentication)")
    public ResponseEntity<?> getAllCourseDetails(@PathVariable Long userId, @PathVariable String code) {
        CourseEntity course = courseService.getCourseByCode(code);
        CourseDTO courseDTO = CourseMapper.toDTO(course);
        courseDTO.setUserId(userId);
        List<SessionDTO> sessionDTOS = sessionService.getDistinctSessionDetailsByCourseCode(code);

        List<StudentEntity> students = enrollmentService.getStudentsByCourseCode(code);
        List<StudentDTO> studentDTOS = StudentMapper.toDto(students);

        List<WeekDTO> weeks = sessionService.getWeeksFromSessions(code);

        List<ProfessorEntity> professors = courseOwnersService.getProfessorsByCourseCode(code);
        List<ProfessorDTO> professorDTOS = ProfessorMapper.toDTO(professors);

        CourseDetailsDTO courseDetailsDTO = new CourseDetailsDTO(courseDTO, sessionDTOS, studentDTOS, weeks, professorDTOS);
        return new ResponseEntity<>(courseDetailsDTO, HttpStatus.OK);
    }

    @PostMapping("/createCourse")
    public ResponseEntity<CourseEntity> createCourse(@RequestBody CourseDTO courseDto) {
        CourseEntity course = courseService.createCourse(courseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(course);
    }

    @DeleteMapping("/deleteCourse/{code}")
    @PreAuthorize("@customSecurityService.canUserAccessByCourseCodeForOwner(#code, authentication)")
    public ResponseEntity<?> deleteCourse(@PathVariable String code) {
        courseService.deleteCourse(code);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/updateCourse/name/{code}")
    @PreAuthorize("@customSecurityService.canUserAccessByCourseCodeForOwner(#code, authentication)")
    public ResponseEntity<?> updateCourseName(@PathVariable String code, @RequestBody CourseDTO courseUpdateDTO) {
        CourseEntity updatedCourse = courseService.updateCourseName(code, courseUpdateDTO.getName());
        return ResponseEntity.ok(updatedCourse);
    }

    @PatchMapping("/updateCourse/location/{userId}/{courseId}")
    @PreAuthorize("@customSecurityService.canUserAccessByUserID(#userId, authentication)")
    public ResponseEntity<?> updateCourseLocation(@PathVariable Long userId, @PathVariable Long courseId, @RequestBody CourseDTO courseUpdateDTO) {
        CourseEntity updatedCourse = courseService.updateLocation(courseId, courseUpdateDTO.getLocation());
        return ResponseEntity.ok(updatedCourse);
    }

    @PatchMapping("/updateCourse/details/{userId}/{code}")
    @PreAuthorize("@customSecurityService.canUserAccessByUserID(#userId, authentication)")
    public ResponseEntity<?> updateCourseDetails(@PathVariable Long userId, @PathVariable String code, @RequestBody CourseDTO courseUpdateDTO) {
        courseService.updateCourseDetails(code, courseUpdateDTO);
        List<WeekDTO> weeks = sessionService.getWeeksFromSessions(code);
        return new ResponseEntity<>(weeks, HttpStatus.OK);
    }

    @PatchMapping("/session/activate/{userId}/{sessionId}")
    @PreAuthorize("@customSecurityService.canUserAccessByUserID(#userId, authentication)")
    public ResponseEntity<?> setSessionActive(@PathVariable Long userId, @PathVariable Long sessionId, @RequestBody SessionDTO sessionDTO) {
        sessionDTO.setId(sessionId);
        return ResponseEntity.ok(sessionService.activateSession(sessionDTO));
    }

    @PatchMapping("/session/deactivate/{userId}/{sessionId}")
    @PreAuthorize("@customSecurityService.canUserAccessByUserID(#userId, authentication)")
    public ResponseEntity<?> setSessionInactive(@PathVariable Long userId, @PathVariable Long sessionId) {
        sessionService.setSessionInactive(sessionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/session/edit/{userId}/{code}/{sessionId}")
    @PreAuthorize("@customSecurityService.canUserAccessByUserID(#userId, authentication)")
    public ResponseEntity<?> editSession(@PathVariable Long userId,@PathVariable String code, @PathVariable Long sessionId, @RequestBody SessionDTO sessionDTO) {
        sessionDTO.setId(sessionId);
        CourseEntity course = courseService.getCourseByCode(code);
        sessionService.editSession(course.getCode(), sessionDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/session/addSession/{userId}/{code}")
    @PreAuthorize("@customSecurityService.canUserAccessByUserID(#userId, authentication)")
    public ResponseEntity<CourseEntity> addSession(@PathVariable Long userId, @PathVariable String code, @RequestBody SessionDTO sessionDTO) {
        CourseEntity course = courseService.getCourseByCode(code);
        sessionService.addSession(course, sessionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping ("/session/deleteSession/{userId}/{code}/{name}")
    @PreAuthorize("@customSecurityService.canUserAccessByUserID(#userId, authentication)")
    public ResponseEntity<CourseEntity> deleteSession(@PathVariable Long userId, @PathVariable String code, @PathVariable String name) {
        sessionService.deleteSessions(code, name);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/session/editSessionDetails/{userId}/{code}/{name}")
    @PreAuthorize("@customSecurityService.canUserAccessByUserID(#userId, authentication)")
    public ResponseEntity<?> editSession(@PathVariable Long userId, @PathVariable String code, @PathVariable String name, @RequestBody SessionDTO sessionDTO) {
        CourseEntity course = courseService.getCourseByCode(code);
        sessionService.editSessionDetails(course, name, sessionDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/session/editWeeks/{userId}/{code}")
    @PreAuthorize("@customSecurityService.canUserAccessByUserID(#userId, authentication)")
    public ResponseEntity<?> editCourseWeeks(@PathVariable Long userId, @PathVariable String code, @RequestBody List<WeekDTO> weeks) {
        CourseEntity course = courseService.getCourseByCode(code);
        sessionService.editSessionDatesByCourseAndWeeks(course, weeks);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/course/deleteStudent/{userId}/{code}/{email}")
    @PreAuthorize("@customSecurityService.canUserAccessByUserID(#userId, authentication)")
    public ResponseEntity<?> removeStudentFromCourse(@PathVariable Long userId, @PathVariable String code, @PathVariable String email) {
        CourseEntity course = courseService.getCourseByCode(code);
        StudentEntity student = studentService.findByEmail(email);

        enrollmentService.removeStudentFromCourse(course, student);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/course/editStudent/{code}")
    @PreAuthorize("@customSecurityService.canUserAccessByCourseCode(#code, authentication)")
    public ResponseEntity<?> editStudentFromCourse(@PathVariable String code, @RequestBody EditStudentInCourseDTO editStudentInCourseDTO) {
        CourseEntity course = courseService.getCourseByCode(code);
        StudentEntity oldStudent = studentService.findByEmail(editStudentInCourseDTO.getOldEmail());
        String newEmail = enrollmentService.editStudentFromCourse(course, oldStudent, editStudentInCourseDTO.getNewEmail());
        return new ResponseEntity<>(newEmail, HttpStatus.OK);
    }

    @PostMapping("/course/addStudents/{userId}/{code}")
    @PreAuthorize("@customSecurityService.canUserAccessByUserID(#userId, authentication)")
    public ResponseEntity<?> addStudentsToCourse(@PathVariable Long userId, @PathVariable String code, @RequestBody String students) {
        CourseEntity course = courseService.getCourseByCode(code);
        List<StudentEntity> studentEntities = enrollmentService.addNewStudentsToCourse(students, course);
        List<StudentDTO> studentDTOS = StudentMapper.toDto(studentEntities);
        return new ResponseEntity<>(studentDTOS, HttpStatus.OK);
    }

    @PostMapping("/course/addProfessor/{code}")
    @PreAuthorize("@customSecurityService.canUserAccessByCourseCodeForOwner(#code, authentication)")
    public ResponseEntity<?> addProfessorToCourse(@PathVariable String code, @RequestBody String professorEmail) {
        ProfessorEntity professor = professorService.findByEmail(professorEmail);
        CourseEntity course = courseService.getCourseByCode(code);
        courseOwnersService.addCourseOwner(course, professor);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/course/deleteProfessor/{code}/{email}")
    @PreAuthorize("@customSecurityService.canUserAccessByCourseCode(#code, authentication)")
    public ResponseEntity<?> removeProfessorFromCourse(@PathVariable String code, @PathVariable String email) {
        CourseEntity course = courseService.getCourseByCode(code);
        ProfessorEntity professor = professorService.findByEmail(email);

        courseOwnersService.removeOwner(course, professor);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/course/existingProfessors/{code}")
    @PreAuthorize("@customSecurityService.canUserAccessByCourseCodeForOwner(#code, authentication)")
    public ResponseEntity<?> getAllExistingProfessorsNotInCourse(@PathVariable String code) {
        List<ProfessorEntity> courseProf = courseOwnersService.getProfessorsByCourseCode(code);
        List<ProfessorEntity> professors = professorService.getAllThatAreNotInList(courseProf);
        List<ProfessorDTO> professorDTOS = new ArrayList<>();
        for(ProfessorEntity professor : professors) {
            professorDTOS.add(new ProfessorDTO(professor.getFirstName(), professor.getLastName(), professor.getEmail()));
        }
        return new ResponseEntity<>(professorDTOS, HttpStatus.OK);
    }

    @GetMapping("/course/export/{code}")
    @PreAuthorize("@customSecurityService.canUserAccessByCourseCode(#code, authentication)")
    public void exportCourseData(@PathVariable String code, HttpServletResponse response) throws IOException {
        CourseEntity course = courseService.getCourseByCode(code);
        response.setContentType("text/csv");
        String fileName = "export_" + course.getName() + ".csv";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        PrintWriter writer = response.getWriter();

        writer.write(exportCourseData.getExportColumns(course));
        writer.write(exportCourseData.getExportContent(course));
        writer.flush();
        writer.close();
    }

    @GetMapping("/attendance/sessionDetails/{userId}/{code}/{name}/{week}")
    @PreAuthorize("@customSecurityService.canUserAccessByCourseCode(#code, authentication)")
    public ResponseEntity<?> getAllSessionDetails(@PathVariable Long userId, @PathVariable String code, @PathVariable String name, @PathVariable int week) {
        SessionEntity session = sessionService.findByCourseCodeNameAndWeek(code, name, week);
        CourseEntity course = session.getCourse();
        CourseDTO courseDTO = CourseMapper.toDTO(course);
        courseDTO.setUserId(userId);
        SessionDTO sessionDTO = SessionMapper.toDTO(session);

        List<StudentDTO> studentDTOs = studentService.findStudentDTOsBySessionAndCode(session, code);

        SessionDetailsDTO sessionDetails = new SessionDetailsDTO(courseDTO, sessionDTO, studentDTOs);
        return new ResponseEntity<>(sessionDetails, HttpStatus.OK);
    }

    @PostMapping("/attendance/markAttendance/{userId}/{code}/{name}/{week}")
    @PreAuthorize("@customSecurityService.canUserAccessByUserID(#userId, authentication)")
    public ResponseEntity<?> markAttendance(@PathVariable Long userId, @PathVariable String code, @PathVariable String name, @PathVariable int week, @RequestBody StudentDTO studentDTO) {
        SessionEntity session = sessionService.findByCourseCodeNameAndWeek(code, name, week);
        StudentEntity student = studentService.findByEmail(studentDTO.getEmail());
        studentDTO = attendanceService.markAttendance(session, student, studentDTO);
        return new ResponseEntity<>(studentDTO, HttpStatus.OK);
    }

    @PostMapping("/attendance/markBulkAttendance/{userId}/{code}/{name}/{week}")
    @PreAuthorize("@customSecurityService.canUserAccessByUserID(#userId, authentication)")
    public ResponseEntity<?> markBulkAttendance(@PathVariable Long userId, @PathVariable String code, @PathVariable String name, @PathVariable int week, @RequestBody AttendanceDTO attendanceDTO) {
        SessionEntity session = sessionService.findByCourseCodeNameAndWeek(code, name, week);
        List<StudentEntity> students = studentService.findStudentsByCourseCodeAndSessionThatAreNotMarked(code, session);
        attendanceDTO = attendanceService.markBulkAttendance(session, students, attendanceDTO);
        return new ResponseEntity<>(attendanceDTO, HttpStatus.OK);
    }

    @PatchMapping("/attendance/saveComment/{userId}/{code}/{name}/{week}")
    @PreAuthorize("@customSecurityService.canUserAccessByUserID(#userId, authentication)")
    public ResponseEntity<?> saveComment(@PathVariable Long userId, @PathVariable String code, @PathVariable String name, @PathVariable int week, @RequestBody StudentDTO studentDTO) {
        SessionEntity session = sessionService.findByCourseCodeNameAndWeek(code, name, week);
        StudentEntity student = studentService.findByEmail(studentDTO.getEmail());
        attendanceService.saveComment(session, student, studentDTO.getAttendance().getComment());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/attendance/statistics/{userId}/{code}/{name}/{week}")
    @PreAuthorize("@customSecurityService.canUserAccessByUserID(#userId, authentication)")
    public ResponseEntity<?> getAttendanceStatistics(@PathVariable Long userId, @PathVariable String code, @PathVariable String name, @PathVariable int week) {
        SessionEntity session = sessionService.findByCourseCodeNameAndWeek(code, name, week);
        AttendanceStatisticsDTO attendanceStatisticsDTO = attendanceService.getAttendanceStatisticsBySession(session);
        return new ResponseEntity<>(attendanceStatisticsDTO, HttpStatus.OK);
    }


}
