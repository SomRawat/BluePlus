package org.bluecollar.bluecollar.session.service;

import org.bluecollar.bluecollar.session.model.UserSession;
import org.bluecollar.bluecollar.session.repository.UserSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.UUID;

@Service
public class SessionService {
    
    @Autowired
    private UserSessionRepository sessionRepository;
    
    public String createSession(String customerId, String clientType) {
        String sessionToken = UUID.randomUUID().toString();
        UserSession session = new UserSession(sessionToken, customerId, clientType);
        sessionRepository.save(session);
        return sessionToken;
    }
    
    public UserSession validateSession(String sessionToken) {
        UserSession session = sessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new RuntimeException("Session not valid"));
        
        if (session.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            sessionRepository.delete(session);
            throw new RuntimeException("Session not valid");
        }
        
        return session;
    }
    
    public void updateCurrentSection(String sessionToken, String section) {
        UserSession session = validateSession(sessionToken);
        session.setCurrentSection(section);
        sessionRepository.save(session);
    }
}