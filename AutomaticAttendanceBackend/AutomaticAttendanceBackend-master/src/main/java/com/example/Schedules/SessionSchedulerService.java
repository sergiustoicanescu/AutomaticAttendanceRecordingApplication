package com.example.Schedules;

import com.example.dto.Session.SessionDTO;
import com.example.dto.Session.SessionMapper;
import com.example.entity.SessionEntity;
import com.example.service.SessionService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SessionSchedulerService {

    private final SessionService sessionService;
    private final SimpMessagingTemplate messagingTemplate;

    public SessionSchedulerService(SessionService sessionService, SimpMessagingTemplate messagingTemplate) {
        this.sessionService = sessionService;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(cron = "0 0/5 * * * *")
    public void checkAndDeactivateSessions() {
        //TODO rethink this so it only starts when there are active sessions
        LocalDateTime now = LocalDateTime.now();
        List<SessionEntity> activeSessions = sessionService.findAllActiveSessions();

        for (SessionEntity session : activeSessions) {
            if (now.isAfter(session.getEndTime().atDate(session.getDate()))) {
                SessionDTO sessionDTO = SessionMapper.toDTO(sessionService.setSessionInactive(session.getId()));
                Long courseId = session.getCourse().getId();
                messagingTemplate.convertAndSend("/topic/course/" + courseId + "/sessionStatus", sessionDTO);
            }
        }
    }
}
