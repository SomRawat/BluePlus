package org.bluecollar.bluecollar.session.interceptor;

import org.bluecollar.bluecollar.session.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SessionInterceptor implements HandlerInterceptor {
    
    @Autowired
    private SessionService sessionService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (request.getRequestURI().startsWith("/api/auth/")) {
            return true; // Skip auth endpoints
        }
        
        String sessionToken = request.getHeader("Session-Token");
        if (sessionToken == null) {
            throw new RuntimeException("Session not valid");
        }
        
        sessionService.validateSession(sessionToken);
        return true;
    }
}