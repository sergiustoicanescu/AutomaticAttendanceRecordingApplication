package com.example.service.impl;

import com.example.entity.*;
import com.example.error.ValidationErrorException;
import com.example.generator.StudentEmailGenerator;
import com.example.repository.CourseEnrollmentEntityRepository;
import com.example.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourseEnrollmentImpl implements EnrollmentService {

    @Autowired
    CourseEnrollmentEntityRepository courseEnrollmentEntityRepository;

    @Autowired
    StudentService studentService;

    @Autowired
    EmailService emailService;

    @Autowired
    ProfessorService professorService;

    @Autowired
    AttendanceService attendanceService;


    @Override
    public void save(CourseEnrollmentEntity courseEnrollmentEntity) {
        courseEnrollmentEntityRepository.save(courseEnrollmentEntity);
    }

    public void enrollStudentsInCourse(List<String> studentEmails, CourseEntity course) {
        List<String> existingStudentEmails = emailService.getAllStudentEmails();

        for (String email : studentEmails) {
            Optional<ProfessorEntity> professor = professorService.findByEmailOptional(email);
            if(professor.isPresent()) {
                throw new ValidationErrorException("Email: " + email + " is already a professor!");
            }
            Optional<StudentEntity> studentOptional = studentService.findByEmailOptional(email);
            StudentEntity studentEntity;

            if (studentOptional.isEmpty()) {
                studentEntity = new StudentEntity();
                studentEntity.setEmail(email);
                studentService.save(studentEntity);
            } else {
                studentEntity = studentOptional.get();
            }

            if (!existingStudentEmails.contains(email)) {
                EmailEntity emailEntity = new EmailEntity();
                emailEntity.setEmail(email);
                emailEntity.setRole(UserRole.STUDENT);
                emailService.save(emailEntity);
            }

            CourseEnrollmentEntity courseEnrollmentEntity = new CourseEnrollmentEntity();
            courseEnrollmentEntity.setCourse(course);
            courseEnrollmentEntity.setStudent(studentEntity);
            save(courseEnrollmentEntity);
        }
    }

    @Override
    public List<StudentEntity> getStudentsByCourseCode(String code) {
         return courseEnrollmentEntityRepository.getStudentsByCourseCode(code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeStudentFromCourse(CourseEntity course, StudentEntity studentEntity) {
        attendanceService.deleteAllAttendancesForStudentByCourse(course, studentEntity);
        CourseEnrollmentEntity enrollmentEntity = courseEnrollmentEntityRepository.findByCourseAndStudent(course, studentEntity);
        if(enrollmentEntity == null) {
            throw new RuntimeException("The student isn't enrolled in this course");
        }
        courseEnrollmentEntityRepository.delete(enrollmentEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<StudentEntity> addNewStudentsToCourse(String studentsText, CourseEntity course) {
        List<String> studentEmails = StudentEmailGenerator.extractEmails(studentsText);
        verifyStudentAlreadyInCourse(course, studentEmails);

        enrollStudentsInCourse(studentEmails, course);

        return courseEnrollmentEntityRepository.getStudentsFromListOfEmails(course, studentEmails);
    }

    private void verifyStudentAlreadyInCourse(CourseEntity course, List<String> studentEmails) {
        List<StudentEntity> enrolledStudents = courseEnrollmentEntityRepository.getStudentsByCourseCode(course.getCode());

        List<String> enrolledStudentEmails = enrolledStudents.stream()
                .map(StudentEntity::getEmail)
                .toList();

        for (String email : studentEmails) {
            if (enrolledStudentEmails.contains(email.toLowerCase())) {
                throw new IllegalArgumentException("The email " + email + " is already enrolled in the course.");
            }
        }
    }

    @Override
    public CourseEnrollmentEntity findEnrollmentByCourseAndStudent(CourseEntity course, StudentEntity student) {
        return courseEnrollmentEntityRepository.findByCourseAndStudent(course, student);
    }

    @Override
    public List<CourseEntity> findCoursesByStudent(StudentEntity student) {
        return courseEnrollmentEntityRepository.findCoursesByStudent(student);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String editStudentFromCourse(CourseEntity course, StudentEntity oldStudent, String newEmail) {
        List<String> studentEmails = StudentEmailGenerator.extractEmails(newEmail);
        if(studentEmails.size() != 1) {
            throw new ValidationErrorException("Please add only one email!");
        }
        verifyStudentAlreadyInCourse(course, studentEmails);
        enrollStudentsInCourse(studentEmails, course);
        StudentEntity newStudent = findStudentByCourseAndEmail(course, newEmail);
        attendanceService.addAttendanceToNewStudentFromOldStudent(course, oldStudent, newStudent);
        removeStudentFromCourse(course, oldStudent);

        return studentService.findByEmail(studentEmails.get(0)).getEmail();
    }

    private StudentEntity findStudentByCourseAndEmail(CourseEntity course, String newEmail) {
        Optional<StudentEntity> studentEntity = courseEnrollmentEntityRepository.findStudentByCourseAndEmail(course, newEmail);
        if(studentEntity.isEmpty()) {
            throw new ValidationErrorException("The student was not enrolled in the course!");
        }
        return studentEntity.get();
    }
}
