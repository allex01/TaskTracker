package com.example.tracker.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final AuditEventRepository auditRepo;

    public LoginFailureHandler(AuditEventRepository auditRepo) {
        this.auditRepo = auditRepo;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");
        AuditEvent ae = new AuditEvent();
        ae.setEventType("LOGIN_FAILURE");
        ae.setAppUser(username);
        if (exception instanceof LockedException) {
            ae.setDetails(java.util.Map.of("reason", "LOCKED"));
            auditRepo.save(ae);
            response.sendRedirect("/auth/login?locked");
        } else {
            ae.setDetails(java.util.Map.of("reason", "BAD_CREDENTIALS"));
            auditRepo.save(ae);
            response.sendRedirect("/auth/login?error");
        }
    }
}


