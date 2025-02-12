package com.example.service;

import com.example.dto.Session.SessionDTO;
import com.example.dto.Course.WeekDTO;
import com.example.entity.CourseEntity;
import com.example.entity.SessionEntity;
import com.example.entity.SessionType;

import java.util.List;

public interface SessionService {
    void save(SessionEntity sessionEntity);

    void createSessions(CourseEntity course, int numberOfWeeks, List<SessionDTO> sessionDTOS);

    List<SessionEntity> getSessionsByCourseCode(String courseCode);

    boolean setSessionActive(Long sessionId);

    SessionEntity setSessionInactive(Long sessionId);

    List<SessionEntity> findAllActiveSessions();

    void editSession(String code, SessionDTO sessionDTO);

    Integer generateAndSaveSessionCode(Long courseId, Long sessionId);

    List<String> getDistinctSessionNamesByCourseCode(String code);

    void updateNumberOfSessions(String code, int initialNrOfWKS, int updatedNrOfWKS);

    void addSession(CourseEntity course, SessionDTO sessionDTO);

    void deleteSessions(String code, String name);

    List<SessionDTO> getDistinctSessionDetailsByCourseCode(String code);

    void editSessionDetails(CourseEntity course, String name, SessionDTO sessionDTO);

    void editSessionDatesByCourseAndWeeks(CourseEntity course, List<WeekDTO> weeks);

    SessionEntity findByCourseCodeNameAndWeek(String code, String name, int week);

    SessionEntity findByCourseCodeAndSessionCode(String courseCode, String sessionCode);

    SessionEntity findByIdAndCode(Long sessionId, String sessionCode);

    List<SessionType> findAllTypesFromACourse(CourseEntity course);

    Integer findCurrentWeek(List<SessionDTO> sessionDTOS);

    SessionDTO activateSession(SessionDTO sessionDTO);

    List<WeekDTO> getWeeksFromSessions(String code);
}
