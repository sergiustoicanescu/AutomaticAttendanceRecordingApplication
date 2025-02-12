package com.example.service.impl;

import com.example.dto.Session.SessionDTO;
import com.example.dto.Course.WeekDTO;
import com.example.entity.CourseEntity;
import com.example.entity.SessionEntity;
import com.example.entity.SessionType;
import com.example.generator.CodeGenerator;
import com.example.repository.SessionEntityRepository;
import com.example.service.SessionService;
import com.example.validators.SessionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SessionEntityImpl implements SessionService {

    @Autowired
    SessionEntityRepository sessionEntityRepository;

    @Override
    public void save(SessionEntity sessionEntity) {
        sessionEntityRepository.save(sessionEntity);
    }

    public void createSessions(CourseEntity course, int numberOfWeeks, List<SessionDTO> sessionDTOS) {
        sessionDTOS.sort(Comparator.comparing(SessionDTO::getStartDate));

        SessionValidator validator = new SessionValidator();
        validator.validate(sessionDTOS, numberOfWeeks);

        LocalDate courseStartDate = sessionDTOS.get(0).getStartDate();
        DayOfWeek courseStartDayOfWeek = courseStartDate.getDayOfWeek();
        for (SessionDTO sessionDTO : sessionDTOS) {
            LocalDate sessionStartDate = sessionDTO.getStartDate();
            long daysFromCourseStart = ChronoUnit.DAYS.between(courseStartDate, sessionStartDate);
            long adjustedDays = daysFromCourseStart + (courseStartDayOfWeek.getValue() - 1);
            int startWeek = (int) Math.floor((double) adjustedDays / 7) + 1;

            for (int week = startWeek; week <= numberOfWeeks; week += sessionDTO.getFrequency()) {
                SessionEntity session = new SessionEntity();
                session.setWeek(week);
                session.setCourse(course);
                session.setName(sessionDTO.getName());
                session.setType(sessionDTO.getType());
                session.setDate(sessionStartDate.plusWeeks(week - startWeek));
                session.setStartTime(sessionDTO.getStartTime());
                session.setEndTime(sessionDTO.getEndTime());
                session.setFrequency(sessionDTO.getFrequency());
                session.setActive(false);
                save(session);
            }
        }
    }

    @Override
    public List<SessionEntity> getSessionsByCourseCode(String courseCode) {
        return sessionEntityRepository.getSessionsByCourseCode(courseCode);
    }

    @Override
    public boolean setSessionActive(Long sessionId) {
        Optional<SessionEntity> session = sessionEntityRepository.findById(sessionId);
        if(session.isPresent()) {
            session.get().setActive(true);
            sessionEntityRepository.save(session.get());
            return true;
        }
        return false;
    }

    @Override
    public SessionEntity setSessionInactive(Long sessionId) {
        Optional<SessionEntity> sessionOpt = sessionEntityRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            return null;
        }
        SessionEntity session = sessionOpt.get();
        session.setActive(false);
        session.setCode(null);
        return sessionEntityRepository.save(session);
    }

    @Override
    public List<SessionEntity> findAllActiveSessions() {
        return sessionEntityRepository.findAllActiveSessions();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editSession(String code, SessionDTO sessionDTO) {
        SessionValidator sessionValidator = new SessionValidator();
        List<SessionEntity> sessions = sessionEntityRepository.getSessionsByCourseCode(code);
        sessionValidator.validateEditSession(sessions, sessionDTO);
        Optional<SessionEntity> session = sessionEntityRepository.findById(sessionDTO.getId());
        if(session.isPresent()) {
            session.get().setDate(sessionDTO.getStartDate());
            session.get().setStartTime(sessionDTO.getStartTime());
            session.get().setEndTime(sessionDTO.getEndTime());
            sessionEntityRepository.save(session.get());
        }
    }

    @Override
    public Integer generateAndSaveSessionCode(Long courseId, Long sessionId) {
        Integer code = generateCode(courseId);
        Optional<SessionEntity> session = sessionEntityRepository.findById(sessionId);
        if(session.isPresent()) {
            session.get().setCode(code);
            sessionEntityRepository.save(session.get());
            return code;
        }
        return null;
    }

    @Override
    public List<String> getDistinctSessionNamesByCourseCode(String code) {
        return sessionEntityRepository.getDistinctSessionNamesByCourseCode(code);
    }

    @Override
    public void updateNumberOfSessions(String code, int initialNrOfWKS, int updatedNrOfWKS) {
        if(initialNrOfWKS == updatedNrOfWKS)
            return;
        List<SessionEntity> sessions = getSessionsByCourseCode(code);
        if(initialNrOfWKS > updatedNrOfWKS) {
            deleteAllSessionsAfterUpdatedWeek(sessions, updatedNrOfWKS);
        }
        else {
            addSessions(code, sessions, updatedNrOfWKS);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSession(CourseEntity course, SessionDTO sessionDTO) {
        SessionValidator sessionValidator = new SessionValidator();
        sessionValidator.validateName(getDistinctSessionNamesByCourseCode(course.getCode()), sessionDTO.getName());
        sessionValidator.verifyStartTimeBeforeEndTime(sessionDTO);
        List<SessionEntity> sessions = getSessionsByCourseCode(course.getCode());

        int dtoWeekOfYear = sessionDTO.getStartDate().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

        Optional<SessionEntity> closestSession = sessions.stream()
                .min(Comparator.comparingInt(session ->
                        Math.abs(session.getDate().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) - dtoWeekOfYear)));

        if (closestSession.isEmpty()) {
            throw new RuntimeException("No sessions available to determine the week!");
        }

        SessionEntity nearestSession = closestSession.get();
        int nearestSessionWeekOfYear = nearestSession.getDate().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int nearestSessionWeek = nearestSession.getWeek();

        long weeksDifference = ChronoUnit.WEEKS.between(
                nearestSession.getDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                sessionDTO.getStartDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));

        int week = nearestSessionWeek + (int) weeksDifference * (dtoWeekOfYear < nearestSessionWeekOfYear ? -1 : 1);

        int frequency = sessionDTO.getFrequency();
        LocalDate startDate = sessionDTO.getStartDate();
        for (int w = week; w <= course.getNumberOfWeeks(); w += frequency) {
            final int currentWeek = w;
            List<SessionEntity> filteredSessions = sessions.stream()
                    .filter(session -> session.getWeek() == currentWeek)
                    .toList();

            for(SessionEntity session : filteredSessions) {
                sessionValidator.checkForOverlapping(sessionDTO, session);
            }
            SessionEntity session = new SessionEntity();
            session.setWeek(w);
            session.setCourse(course);
            session.setName(sessionDTO.getName());
            session.setType(sessionDTO.getType());
            session.setDate(startDate);
            session.setStartTime(sessionDTO.getStartTime());
            session.setEndTime(sessionDTO.getEndTime());
            session.setFrequency(sessionDTO.getFrequency());
            session.setActive(false);
            save(session);

            LocalDate nextWeekDate = sessions.stream()
                    .filter(s -> s.getWeek() == currentWeek + 1)
                    .map(SessionEntity::getDate)
                    .findFirst()
                    .orElse(null);
            if (nextWeekDate != null) {
                long weeksBetween = ChronoUnit.WEEKS.between(startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                        nextWeekDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
                startDate = startDate.plusWeeks(weeksBetween - 1);
            }
            startDate = startDate.plusWeeks(frequency);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSessions(String code, String name) {
        if(getDistinctSessionNamesByCourseCode(code).size() <= 1) {
            throw new RuntimeException("Cannot delete the last session!");
        }
        List<SessionEntity> sessions = sessionEntityRepository.findAllSessionsByCodeAndName(code, name);
        sessionEntityRepository.deleteAll(sessions);
    }

    @Override
    public List<SessionDTO> getDistinctSessionDetailsByCourseCode(String code) {
        List<SessionDTO> sessionDTOS = new ArrayList<>();
        List<Object[]> sessionNames = sessionEntityRepository.getDistinctSessionDetailsByCourseCode(code);
        for(Object[] session : sessionNames) {
            SessionDTO sessionDTO = new SessionDTO((String) session[0], (SessionType) session[1], (int) session[2]);
            sessionDTO.setName((String) session[0]);
            sessionDTO.setType((SessionType) session[1]);
            sessionDTO.setFrequency((int) session[2]);
            sessionDTOS.add(sessionDTO);
        }
        return sessionDTOS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editSessionDetails(CourseEntity course, String name, SessionDTO sessionDTO) {
        SessionValidator sessionValidator = new SessionValidator();
        if(!name.equals(sessionDTO.getName())) {
            sessionValidator.validateName(sessionEntityRepository.getDistinctSessionNamesByCourseCode(course.getCode()), sessionDTO.getName());

        }

        List<SessionEntity> existingSessions = sessionEntityRepository.findAllSessionsByCodeAndName(course.getCode(), name);
        if(existingSessions.isEmpty()) {
            throw new RuntimeException("No sessions found with this name " + name + "!");
        }

        int oldFrequency = existingSessions.get(0).getFrequency();
        int newFrequency = sessionDTO.getFrequency();

        existingSessions.forEach(session -> {
            session.setName(sessionDTO.getName());
            session.setType(sessionDTO.getType());
            session.setFrequency(newFrequency);
        });

        sessionEntityRepository.saveAll(existingSessions);

        if (newFrequency != oldFrequency) {
            if (newFrequency == 1) {
                addWeeklySessions(course, existingSessions, sessionDTO);
            } else if (newFrequency == 2) {
                removeBiWeeklySessions(course, existingSessions);
            }
        }
    }

    @Override
    public void editSessionDatesByCourseAndWeeks(CourseEntity course, List<WeekDTO> weeks) {
        List<WeekDTO> filteredWeeks = weeks.stream().filter(weekDTO -> weekDTO.getDifference() != 0).toList();
        for(WeekDTO week : filteredWeeks) {
            List<SessionEntity> sessions = sessionEntityRepository.getSessionsByCourseIdAndWeek(course.getId(), week.getWeekNumber());
            for(SessionEntity session : sessions) {
                session.setDate(session.getDate().plusWeeks(week.getDifference()));
                sessionEntityRepository.save(session);
            }
        }
    }

    @Override
    public SessionEntity findByCourseCodeNameAndWeek(String code, String name, int week) {
        return sessionEntityRepository.findByCourseCodeNameAndWeek(code, name, week);
    }

    @Override
    public SessionEntity findByCourseCodeAndSessionCode(String courseCode, String sessionCode) {
        Optional<SessionEntity> optionalSession = sessionEntityRepository.findByCourseCodeAndSessionCode(courseCode, sessionCode);
        if(optionalSession.isEmpty()) {
            throw new RuntimeException("Session couldn't be found!");
        }

        return optionalSession.get();
    }

    @Override
    public SessionEntity findByIdAndCode(Long sessionId, String sessionCode) {
        Optional<SessionEntity> optionalSession = sessionEntityRepository.findByIdAndCode(sessionId, Integer.valueOf(sessionCode));
        if(optionalSession.isPresent()) {
            return optionalSession.get();
        } else {
            throw new RuntimeException("Couldn't find the session!");
        }
    }

    @Override
    public List<SessionType> findAllTypesFromACourse(CourseEntity course) {
        return sessionEntityRepository.findAllTypesFromACourse(course);
    }

    @Override
    public Integer findCurrentWeek(List<SessionDTO> sessionDTOS) {
        LocalDate now = LocalDate.now();
        Optional<Integer> currentWeek = sessionDTOS.stream()
                .filter(sessionDTO -> {
                    LocalDate sessionStart = sessionDTO.getStartDate();
                    LocalDate startOfWeek = sessionStart.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                    LocalDate endOfWeek = sessionStart.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                    return !now.isBefore(startOfWeek) && !now.isAfter(endOfWeek);
                })
                .findAny()
                .map(SessionDTO::getWeek);
        return currentWeek.orElse(null);
    }

    @Override
    public SessionDTO activateSession(SessionDTO sessionDTO) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDate currentDate = currentTime.toLocalDate();
        LocalTime currentTimeOnly = currentTime.toLocalTime();

        LocalDate sessionDate = sessionDTO.getStartDate();
        LocalTime sessionStartTime = sessionDTO.getStartTime();
        LocalTime sessionEndTime = sessionDTO.getEndTime();

        boolean isActive = sessionDTO.isActive();
        Integer code = sessionDTO.getCode();
        if (!isActive && !currentDate.isBefore(sessionDate) && !currentDate.isAfter(sessionDate)) {
            if (currentTimeOnly.isAfter(sessionStartTime) && currentTimeOnly.isBefore(sessionEndTime)) {
                code = generateAndSaveSessionCode(sessionDTO.getCourseId(), sessionDTO.getId());
                isActive = setSessionActive(sessionDTO.getId());
            }
        }

        sessionDTO.setActive(isActive);
        sessionDTO.setCode(code);

        return sessionDTO;
    }

    @Override
    public List<WeekDTO> getWeeksFromSessions(String code) {
        List<SessionEntity> sessions = getSessionsByCourseCode(code);
        List<WeekDTO> result = new ArrayList<>();
        Map<Integer, List<SessionEntity>> sessionsByWeek = sessions.stream()
                .collect(Collectors.groupingBy(SessionEntity::getWeek));
        sessionsByWeek.forEach((week, sessionsInWeek) -> {
            WeekDTO weekDTO = new WeekDTO();
            weekDTO.setWeekNumber(week);
            if (!sessionsInWeek.isEmpty()) {
                Map<LocalDate, Long> weekDayCount = sessionsInWeek.stream()
                        .collect(Collectors.groupingBy(session -> session.getDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)), Collectors.counting()));

                LocalDate mostCommonWeekStart = Collections.max(weekDayCount.entrySet(), Map.Entry.comparingByValue()).getKey();

                LocalDate lastDayOfWeek = mostCommonWeekStart.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

                weekDTO.setFirstDay(mostCommonWeekStart);
                weekDTO.setLastDay(lastDayOfWeek);
            }
            result.add(weekDTO);
        });

        return result;
    }

    private void addWeeklySessions(CourseEntity course, List<SessionEntity> existingSessions, SessionDTO sessionDTO) {
        SessionValidator sessionValidator = new SessionValidator();
        List<SessionEntity> allSessions = sessionEntityRepository.getSessionsByCourseCode(course.getCode());
        int startWeek = existingSessions.get(0).getWeek() + 1;
        int endWeek = course.getNumberOfWeeks();
        for(int week = startWeek; week <= endWeek; week+=2) {
            int lastWeek = week - 1;
            Optional<SessionEntity> lastSession = existingSessions.stream().filter(session -> session.getWeek() == lastWeek).findFirst();
            if(lastSession.isEmpty()) {
                throw new RuntimeException("This shouldn't happen, contact the developer!");
            }
            LocalDate date = lastSession.get().getDate();

            LocalDate currentWeekDate = allSessions.stream()
                    .filter(s -> s.getWeek() == lastWeek + 1)
                    .map(SessionEntity::getDate)
                    .findFirst()
                    .orElse(null);
            if (currentWeekDate != null) {
                long weeksBetween = ChronoUnit.WEEKS.between(date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                        currentWeekDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
                sessionDTO.setStartDate(date.plusWeeks(weeksBetween));
            } else {
                sessionDTO.setStartDate(date.plusWeeks(1));
            }
            sessionDTO.setStartTime(lastSession.get().getStartTime());
            sessionDTO.setEndTime(lastSession.get().getEndTime());

            int finalWeek = week;
            List<SessionEntity> matchedSessions = allSessions.stream()
                    .filter(session -> session.getWeek() == finalWeek)
                    .toList();

            for(SessionEntity session : matchedSessions) {
                sessionValidator.checkForOverlapping(sessionDTO, session);
            }

            SessionEntity newSession = new SessionEntity();
            newSession.setWeek(week);
            newSession.setCourse(course);
            newSession.setName(sessionDTO.getName());
            newSession.setType(sessionDTO.getType());
            newSession.setDate(sessionDTO.getStartDate());
            newSession.setStartTime(sessionDTO.getStartTime());
            newSession.setEndTime(sessionDTO.getEndTime());
            newSession.setFrequency(sessionDTO.getFrequency());
            newSession.setActive(false);
            save(newSession);

        }
    }

    private void removeBiWeeklySessions(CourseEntity course, List<SessionEntity> existingSessions) {
        int startWeek = existingSessions.get(0).getWeek() + 1;
        int endWeek = course.getNumberOfWeeks();
        for(int week = startWeek; week <= endWeek; week+=2) {
            int finalWeek = week;
            List<SessionEntity> matchedSessions = existingSessions.stream()
                    .filter(session -> session.getWeek() == finalWeek)
                    .toList();
            sessionEntityRepository.deleteAll(matchedSessions);
        }
    }

    private void addSessions(String code, List<SessionEntity> sessions, int updatedNrOfWKS) {
        List<String> distinctSessions = getDistinctSessionNamesByCourseCode(code);

        LinkedHashMap<String, SessionEntity> lastSessionsMap = new LinkedHashMap<>();
        ListIterator<SessionEntity> listIterator = sessions.listIterator(sessions.size());
        while (listIterator.hasPrevious()) {
            SessionEntity session = listIterator.previous();
            if (distinctSessions.contains(session.getName()) && !lastSessionsMap.containsKey(session.getName())) {
                lastSessionsMap.put(session.getName(), session);
            }
        }

        List<SessionEntity> lastSessions = new ArrayList<>(lastSessionsMap.values());
        for(SessionEntity session : lastSessions) {
            int week = session.getWeek();
            LocalDate date = session.getDate();
            int frequency = session.getFrequency();
            while(week + frequency <= updatedNrOfWKS) {
                date = date.plusWeeks(frequency);
                week+=frequency;

                SessionEntity newSession = new SessionEntity();
                newSession.setWeek(week);
                newSession.setCourse(session.getCourse());
                newSession.setName(session.getName());
                newSession.setType(session.getType());
                newSession.setDate(date);
                newSession.setStartTime(session.getStartTime());
                newSession.setEndTime(session.getEndTime());
                newSession.setFrequency(frequency);
                newSession.setActive(false);
                save(newSession);
            }
        }
    }

    private void deleteAllSessionsAfterUpdatedWeek(List<SessionEntity> sessions, int updatedNrOfWKS) {
        for(SessionEntity session : sessions) {
            if(session.getWeek() > updatedNrOfWKS) {
                sessionEntityRepository.delete(session);
            }
        }
    }

    private Integer generateCode(Long courseId) {
        List<Integer> existingCodes = sessionEntityRepository.getCodes(courseId);
        Integer code;
        do {
            code = CodeGenerator.generateSessionCode();
        } while (existingCodes.contains(code));

        return code;
    }
}
