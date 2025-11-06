package com.example.tracker.security;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "security_settings")
public class SecuritySettings {
    @Id
    private Short id = 1;

    @Column(name = "max_failed_attempts", nullable = false)
    private int maxFailedAttempts = 3;

    @Column(name = "max_failed_attempts_cap", nullable = false)
    private int maxFailedAttemptsCap = 10;

    @Column(name = "lock_minutes", nullable = false)
    private int lockMinutes = 15;

    @Column(name = "session_idle_minutes", nullable = false)
    private int sessionIdleMinutes = 15;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public Short getId() { return id; }
    public void setId(Short id) { this.id = id; }
    public int getMaxFailedAttempts() { return maxFailedAttempts; }
    public void setMaxFailedAttempts(int maxFailedAttempts) { this.maxFailedAttempts = maxFailedAttempts; }
    public int getMaxFailedAttemptsCap() { return maxFailedAttemptsCap; }
    public void setMaxFailedAttemptsCap(int maxFailedAttemptsCap) { this.maxFailedAttemptsCap = maxFailedAttemptsCap; }
    public int getLockMinutes() { return lockMinutes; }
    public void setLockMinutes(int lockMinutes) { this.lockMinutes = lockMinutes; }
    public int getSessionIdleMinutes() { return sessionIdleMinutes; }
    public void setSessionIdleMinutes(int sessionIdleMinutes) { this.sessionIdleMinutes = sessionIdleMinutes; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}


