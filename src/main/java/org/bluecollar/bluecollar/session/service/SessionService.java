package org.bluecollar.bluecollar.session.service;

import org.bluecollar.bluecollar.session.model.UserSession;
import org.bluecollar.bluecollar.common.util.SecurityUtil;
import org.bluecollar.bluecollar.common.exception.UnauthorizedException;
import org.bluecollar.bluecollar.session.repository.UserSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class SessionService {
    
    private final UserSessionRepository sessionRepository;

    @Autowired
    public SessionService(UserSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }
    
    public String createSession(String customerId, String clientType) {
        String sessionToken = SecurityUtil.generateSecureToken();
        UserSession session = new UserSession(sessionToken, customerId, clientType);
        sessionRepository.save(session);
        return sessionToken;
    }
    
    public UserSession validateSession(String sessionToken) {
        UserSession session = sessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid or expired session token."));
        
        if (session.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            sessionRepository.delete(session);
            throw new UnauthorizedException("Invalid or expired session token.");
        }
        
        return session;
    }
    
    public void updateCurrentSection(String sessionToken, String section) {
        UserSession session = validateSession(sessionToken);
        session.setCurrentSection(section);
        sessionRepository.save(session);
    }
    
    public String getCustomerIdFromToken(String sessionToken) {
        UserSession session = validateSession(sessionToken);
        return session.getCustomerId();
    }
}