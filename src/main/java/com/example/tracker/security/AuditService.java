package com.example.tracker.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;

@Service
public class AuditService {
    private final AuditEventRepository auditRepo;

    public AuditService(AuditEventRepository auditRepo) {
        this.auditRepo = auditRepo;
    }

    public void log(String eventType, String appUser, HttpServletRequest request, Map<String, Object> details) {
        AuditEvent e = new AuditEvent();
        e.setEventType(eventType);
        e.setAppUser(appUser);
        if (request != null) {
            e.setWorkstation(request.getRemoteAddr());
            e.setOsUser(request.getRemoteUser());
        } else {
            e.setOsUser(System.getProperty("user.name"));
        }
        e.setOccurredAt(OffsetDateTime.now());
        e.setDetails(details);
        auditRepo.save(e);
    }
}


