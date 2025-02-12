package com.example.service.impl;

import com.example.dto.Attendance.AttendanceDTO;
import com.example.dto.Attendance.AttendanceStatisticsDTO;
import com.example.dto.Location.LocationDTO;
import com.example.dto.Student.StudentDTO;
import com.example.entity.*;
import com.example.error.ValidationErrorException;
import com.example.repository.AttendanceEntityRepository;
import com.example.service.AttendanceService;
import com.example.utils.LocationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceEntityImpl implements AttendanceService {

    private static final double ATTENDANCE_THRESHOLD_KM = 0.1;

    @Autowired
    AttendanceEntityRepository attendanceEntityRepository;

    @Override
    @Transactional
    public StudentDTO markAttendance(SessionEntity session, StudentEntity student, StudentDTO studentDTO) {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        LocalTime timeWithHoursAndMinutesOnly = LocalTime.of(currentTime.getHour(), currentTime.getMinute());
        Optional<AttendanceEntity> optionalAttendance = attendanceEntityRepository.findBySessionAndStudentForUpdate(session, student);
        AttendanceEntity attendance;
        if(optionalAttendance.isPresent()) {
            attendance = optionalAttendance.get();
        } else {
            attendance = new AttendanceEntity();
            attendance.setSession(session);
            attendance.setStudent(student);
        }
        attendance.setAttendanceStatus(studentDTO.getAttendance().getStatus());
        attendance.setDate(currentDate);
        attendance.setTime(timeWithHoursAndMinutesOnly);
        attendanceEntityRepository.save(attendance);

        studentDTO.getAttendance().setDate(currentDate);
        studentDTO.getAttendance().setTime(timeWithHoursAndMinutesOnly);
        return studentDTO;
    }

    @Override
    @Transactional
    public AttendanceDTO markBulkAttendance(SessionEntity session, List<StudentEntity> students, AttendanceDTO attendanceDTO) {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        LocalTime timeWithHoursAndMinutesOnly = LocalTime.of(currentTime.getHour(), currentTime.getMinute());

        List<AttendanceEntity> attendances = new ArrayList<>();
        for(StudentEntity student : students) {
            AttendanceEntity attendance = new AttendanceEntity();
            attendance.setAttendanceStatus(attendanceDTO.getStatus());
            attendance.setStudent(student);
            attendance.setSession(session);
            attendance.setDate(currentDate);
            attendance.setTime(timeWithHoursAndMinutesOnly);
            attendances.add(attendance);
        }

        attendanceEntityRepository.saveAll(attendances);

        attendanceDTO.setDate(currentDate);
        attendanceDTO.setTime(timeWithHoursAndMinutesOnly);

        return attendanceDTO;
    }

    @Override
    public void saveComment(SessionEntity session, StudentEntity student, String comment) {
        Optional<AttendanceEntity> optionalAttendance = attendanceEntityRepository.findBySessionAndStudent(session, student);
        AttendanceEntity attendance;
        if(optionalAttendance.isPresent()) {
            attendance = optionalAttendance.get();
        } else {
            attendance = new AttendanceEntity();
            attendance.setSession(session);
            attendance.setStudent(student);
        }
        attendance.setComment(comment);
        attendanceEntityRepository.save(attendance);
    }

    @Override
    public AttendanceStatisticsDTO getAttendanceStatisticsBySession(SessionEntity session) {
        return attendanceEntityRepository.getAttendanceStatisticsBySession(session);
    }

    @Override
    public boolean verifyLocationProximity(double userLat, double userLng, double courseLat, double courseLng) {
        double distance = LocationUtil.calculateDistance(userLat, userLng, courseLat, courseLng);

        return distance <= ATTENDANCE_THRESHOLD_KM;
    }

    @Override
    public boolean verifyAttendance(SessionEntity session, StudentEntity student) {
        List<AttendanceEntity> attendances = attendanceEntityRepository.findAllPresentAttendancesForStudentInAWeek(session.getCourse(), student, session.getWeek(), session.getType(), AttendanceStatus.PRESENT);
        return attendances.isEmpty();
    }

    @Override
    public void markStudentPresent(SessionEntity session, StudentEntity student) {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        LocalTime timeWithHoursAndMinutesOnly = LocalTime.of(currentTime.getHour(), currentTime.getMinute());

        Optional<AttendanceEntity> optionalAttendance = attendanceEntityRepository.findBySessionAndStudent(session, student);
        AttendanceEntity attendance = optionalAttendance.orElseGet(AttendanceEntity::new);
        attendance.setAttendanceStatus(AttendanceStatus.PRESENT);
        attendance.setStudent(student);
        attendance.setSession(session);
        attendance.setDate(currentDate);
        attendance.setTime(timeWithHoursAndMinutesOnly);
        attendanceEntityRepository.save(attendance);
    }

    @Override
    public Long countPresentByStudentTypeAndCourse(StudentEntity student, SessionType type, CourseEntity course) {
        return attendanceEntityRepository.countPresentByStudentTypeAndCourse(student, type, course);
    }

    @Override
    public void deleteAllAttendancesForStudentByCourse(CourseEntity course, StudentEntity studentEntity) {
        List<AttendanceEntity> attendanceEntities = attendanceEntityRepository.findByStudentAndCourse(studentEntity, course);
        attendanceEntityRepository.deleteAll(attendanceEntities);
    }

    @Override
    public Optional<AttendanceEntity> findByStudentAndSession(StudentEntity student, SessionEntity session) {
        return attendanceEntityRepository.findBySessionAndStudent(session, student);
    }

    @Override
    public void addAttendanceToNewStudentFromOldStudent(CourseEntity course, StudentEntity oldStudent, StudentEntity newStudent) {
        List<AttendanceEntity> attendances = attendanceEntityRepository.findByStudentAndCourse(oldStudent, course);
        for(AttendanceEntity attendance : attendances) {
            AttendanceEntity newAttendance = new AttendanceEntity();
            newAttendance.setStudent(newStudent);
            newAttendance.setSession(attendance.getSession());
            newAttendance.setDate(attendance.getDate());
            newAttendance.setTime(attendance.getTime());
            newAttendance.setAttendanceStatus(attendance.getAttendanceStatus());
            newAttendance.setComment(attendance.getComment());
            attendanceEntityRepository.save(newAttendance);
        }
    }

    @Override
    public void markAttendanceForStudent(SessionEntity session, StudentEntity student, LocationDTO userLocation) {
        if(!session.isActive()) {
            throw new ValidationErrorException("This session is inactive!");
        }

        String location = session.getCourse().getLocation();
        double userLat = 0, userLng = 0, courseLat = 0, courseLng = 0;
        boolean verifyLocation = true;

        if (location != null) {
            LocationDTO locationDTO = new LocationDTO(location);

            try {
                userLat = Double.parseDouble(userLocation.getLat());
                userLng = Double.parseDouble(userLocation.getLng());
                courseLat = Double.parseDouble(locationDTO.getLat());
                courseLng = Double.parseDouble(locationDTO.getLng());
            } catch (NumberFormatException e) {
                throw new RuntimeException("Couldn't convert coordinates!");
            }
        } else {
            verifyLocation = false;
        }

        if (verifyLocation && !verifyLocationProximity(userLat, userLng, courseLat, courseLng)) {
            throw new ValidationErrorException("The user is not present in the classroom!");
        }

        markStudentPresent(session, student);
    }
}
