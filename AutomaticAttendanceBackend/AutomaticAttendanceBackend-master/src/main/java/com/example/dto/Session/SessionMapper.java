package com.example.dto.Session;

import com.example.entity.SessionEntity;

import java.util.ArrayList;
import java.util.List;

public class SessionMapper {

    public static SessionDTO toDTO(SessionEntity session) {
        return new SessionDTO(
                session.getId(),
                session.getName(),
                session.getType(),
                session.getDate(),
                session.getStartTime(),
                session.isActive(),
                session.getWeek(),
                session.getFrequency(),
                session.getEndTime(),
                session.getCourse().getId(),
                session.getCode()
        );
    }

    public static List<SessionDTO> toDTO(List<SessionEntity> sessions) {
        List<SessionDTO> sessionDTOS = new ArrayList<>();
        for(SessionEntity session : sessions) {
            SessionDTO sessionDTO = toDTO(session);
            sessionDTOS.add(sessionDTO);
        }
        return sessionDTOS;
    }

}
