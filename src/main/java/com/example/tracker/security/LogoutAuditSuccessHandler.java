package com.example.tracker.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LogoutAuditSuccessHandler implements LogoutSuccessHandler {

    private final AuditEventRepository auditRepo;

    public LogoutAuditSuccessHandler(AuditEventRepository auditRepo) {
        this.auditRepo = auditRepo;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = authentication != null ? authentication.getName() : null;
        AuditEvent ae = new AuditEvent();
        ae.setEventType("LOGOUT");
        ae.setAppUser(username);
        auditRepo.save(ae);
        response.sendRedirect("/auth/login");
    }
}


