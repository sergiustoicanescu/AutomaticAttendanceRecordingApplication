package com.example.repository;

import com.example.entity.CourseEntity;
import com.example.entity.CourseOwnersEntity;
import com.example.entity.ProfessorEntity;
import com.example.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseOwnersEntityRepository extends JpaRepository<CourseOwnersEntity, Long> {

    @Query("SELECT co.course FROM CourseOwnersEntity co where co.professor.userEntity.id = :userId")
    List<CourseEntity> getCoursesByUserId(@Param("userId") Long userId);

    @Query("SELECT co.professor FROM CourseOwnersEntity co where co.course.code = :code ORDER BY co.id ASC")
    List<ProfessorEntity> getProfessorsByCourseCode(@Param("code") String code);

    @Query("SELECT co FROM CourseOwnersEntity co where co.course = :course and co.professor.userEntity = :user")
    Optional<Object> findByCourseAndUser(@Param("course") CourseEntity courseEntity,@Param("user") UserEntity user);

    @Query("SELECT co FROM CourseOwnersEntity co where co.course = :course and co.professor = :professor")
    Optional<CourseOwnersEntity> findByCourseAndProfessor(@Param("course") CourseEntity course,@Param("professor") ProfessorEntity professor);
}
