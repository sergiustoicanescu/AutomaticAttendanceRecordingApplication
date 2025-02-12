package com.example.validators;

import com.example.dto.Session.SessionDTO;
import com.example.entity.SessionEntity;
import com.example.error.ValidationErrorException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class SessionValidator {

    public void validateName(List<String> distinctSessionNamesByCourseCode, String name) {
        if(distinctSessionNamesByCourseCode.contains(name)){
            throw new ValidationErrorException("There is already a session with this name!");
        }
    }

    private static class SessionOccurrence {
        LocalDate date;
        LocalTime startTime;
        LocalTime endTime;

        SessionOccurrence(LocalDate date, LocalTime startTime, LocalTime endTime) {
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        boolean overlapsWith(SessionOccurrence other) {
            return this.startTime.isBefore(other.endTime) && this.endTime.isAfter(other.startTime);
        }
    }

    public void validate(List<SessionDTO> sessionDTOS, int numberOfWeeks) {
        List<SessionOccurrence> allOccurrences = generateAllOccurrences(sessionDTOS, numberOfWeeks);

        allOccurrences.sort(Comparator.comparing((SessionOccurrence o) -> o.date)
                .thenComparing(o -> o.startTime));

        for (int i = 0; i < allOccurrences.size() - 1; i++) {
            SessionOccurrence current = allOccurrences.get(i);
            SessionOccurrence next = allOccurrences.get(i + 1);

            if (current.overlapsWith(next) && current.date.isEqual(next.date)) {
                throw new ValidationErrorException("Session times overlap.");
            }
        }
    }

    private List<SessionOccurrence> generateAllOccurrences(List<SessionDTO> sessionDTOS, int numberOfWeeks) {
        List<SessionOccurrence> occurrences = new ArrayList<>();
        for (SessionDTO sessionDTO : sessionDTOS) {
            LocalDate startDate = sessionDTO.getStartDate();
            int frequency = sessionDTO.getFrequency();

            for (int week = 0; week < numberOfWeeks; week += frequency) {
                LocalDate occurrenceDate = startDate.plusWeeks(week);
                occurrences.add(new SessionOccurrence(occurrenceDate, sessionDTO.getStartTime(), sessionDTO.getEndTime()));
            }
        }
        return occurrences;
    }

    public void validateEditSession(List<SessionEntity> sessionEntities, SessionDTO sessionDTO) {
        verifyStartTimeBeforeEndTime(sessionDTO);

        for(SessionEntity sessionEntity : sessionEntities) {
            checkForOverlapping(sessionDTO, sessionEntity);
        }
    }

    public void checkForOverlapping(SessionDTO sessionDTO, SessionEntity sessionEntity) {
        if(Objects.equals(sessionEntity.getId(), sessionDTO.getId()))
            return;
        if(sessionEntity.getDate().equals(sessionDTO.getStartDate())) {
            boolean isOverlap = sessionEntity.getStartTime().isBefore(sessionDTO.getEndTime()) && sessionEntity.getEndTime().isAfter(sessionDTO.getStartTime());
            boolean isSameStartTime = sessionEntity.getStartTime().equals(sessionDTO.getStartTime());

            if(isOverlap || isSameStartTime) {
                throw new ValidationErrorException("Session times overlap!");
            }
        }
    }

    public void verifyStartTimeBeforeEndTime(SessionDTO sessionDTO) {
        if(sessionDTO.getStartTime().isAfter(sessionDTO.getEndTime()) || sessionDTO.getStartTime().equals(sessionDTO.getEndTime())) {
            throw new ValidationErrorException("Session start time must be before the end time!");
        }
    }
}

