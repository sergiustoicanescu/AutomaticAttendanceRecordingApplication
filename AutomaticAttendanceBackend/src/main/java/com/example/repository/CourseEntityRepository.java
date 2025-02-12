package com.example.repository;

import com.example.entity.CourseEntity;
import com.example.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseEntityRepository extends JpaRepository<CourseEntity, Long> {
    @Query("SELECT c FROM CourseEntity c WHERE c.professor.userEntity.id = :userId")
    List<CourseEntity> findCoursesByProfessorUserId(@Param("userId") Long userId);

    @Query("SELECT c.code FROM CourseEntity c")
    List<String> getCodes();

    @Query("SELECT c from CourseEntity c WHERE c.code = :courseId")
    Optional<CourseEntity> getCourseByCode(@Param("courseId") String courseId);

    @Query("SELECT c FROM CourseEntity c WHERE c.code = :code AND c.professor.userEntity = :user")
    Optional<Object> findByCodeAndUser(@Param("code") String code,@Param("user") UserEntity user);
}
