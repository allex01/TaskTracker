package com.example.tracker.security;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_events")
public class AuditEvent {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt = OffsetDateTime.now();

    @Column(name = "event_type", nullable = false, length = 64)
    private String eventType;

    @Column(name = "app_user")
    private String appUser;

    @Column(name = "os_user")
    private String osUser;

    @Column(name = "workstation")
    private String workstation;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "details", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private java.util.Map<String, Object> details;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public OffsetDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(OffsetDateTime occurredAt) { this.occurredAt = occurredAt; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getAppUser() { return appUser; }
    public void setAppUser(String appUser) { this.appUser = appUser; }
    public String getOsUser() { return osUser; }
    public void setOsUser(String osUser) { this.osUser = osUser; }
    public String getWorkstation() { return workstation; }
    public void setWorkstation(String workstation) { this.workstation = workstation; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public java.util.Map<String, Object> getDetails() { return details; }
    public void setDetails(java.util.Map<String, Object> details) { this.details = details; }
}


