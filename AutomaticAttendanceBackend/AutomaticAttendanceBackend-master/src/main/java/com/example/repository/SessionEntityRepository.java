package com.example.repository;

import com.example.entity.CourseEntity;
import com.example.entity.SessionEntity;
import com.example.entity.SessionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SessionEntityRepository extends JpaRepository<SessionEntity, Long> {
    @Query("SELECT s FROM SessionEntity s WHERE s.course.code = :courseCode ORDER BY s.week ASC, s.date ASC")
    List<SessionEntity> getSessionsByCourseCode(@Param("courseCode") String courseCode);

    @Query("SELECT s FROM SessionEntity s where s.active = true")
    List<SessionEntity> findAllActiveSessions();

    @Query("SELECT s FROM SessionEntity s where s.course.id = :courseId and s.week = :week")
    List<SessionEntity> getSessionsByCourseIdAndWeek(@Param("courseId") Long courseId, @Param("week") int week);

    @Query("SELECT s.code FROM SessionEntity s where s.course.id = :courseId and s.code is not null")
    List<Integer> getCodes(@Param("courseId") Long courseId);
    @Query("SELECT DISTINCT s.name FROM SessionEntity s WHERE s.course.code = :code")
    List<String> getDistinctSessionNamesByCourseCode(@Param("code") String code);

    @Query("SELECT s FROM SessionEntity s WHERE s.course.code = :code and s.name = :name ORDER BY s.week ASC")
    List<SessionEntity> findAllSessionsByCodeAndName(@Param("code") String code,@Param("name") String name);
    @Query("SELECT DISTINCT s.name, s.type, s.frequency FROM SessionEntity s where s.course.code = :code")
    List<Object[]> getDistinctSessionDetailsByCourseCode(@Param("code") String code);

    @Query("SELECT s FROM SessionEntity s where s.course.code = :code and s.name = :name and s.week = :week")
    SessionEntity findByCourseCodeNameAndWeek(@Param("code") String code, @Param("name") String name, @Param("week") int week);

    @Query("SELECT s FROM SessionEntity s where s.course.code = :courseCode and s.code = :sessionCode and s.active = true")
    Optional<SessionEntity> findByCourseCodeAndSessionCode(String courseCode, String sessionCode);

    @Query("SELECT s FROM SessionEntity s WHERE s.id = :id AND s.code = :sessionCode")
    Optional<SessionEntity> findByIdAndCode(@Param("id") Long sessionId,@Param("sessionCode") Integer sessionCode);

    @Query("SELECT DISTINCT s.type FROM SessionEntity s where s.course = :course")
    List<SessionType> findAllTypesFromACourse(@Param("course") CourseEntity course);
}
