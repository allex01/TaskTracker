package com.example.tracker.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AppLifecycleListener {

    private final AuditService auditService;

    public AppLifecycleListener(AuditService auditService) {
        this.auditService = auditService;
    }

    @EventListener
    public void onStart(ContextRefreshedEvent event) {
        auditService.log("APP_START", null, (HttpServletRequest) null, null);
    }

    @EventListener
    public void onStop(ContextClosedEvent event) {
        auditService.log("APP_STOP", null, (HttpServletRequest) null, null);
    }
}


