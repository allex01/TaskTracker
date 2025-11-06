package com.example.tracker.security;

import com.example.tracker.user.entity.User;
import com.example.tracker.user.repo.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
public class AuthEventsListener implements ApplicationListener<org.springframework.context.ApplicationEvent> {

    private final UserRepository userRepository;
    private final AuditEventRepository auditRepo;
    private final SecuritySettingsRepository settingsRepo;

    public AuthEventsListener(UserRepository userRepository, AuditEventRepository auditRepo, SecuritySettingsRepository settingsRepo) {
        this.userRepository = userRepository;
        this.auditRepo = auditRepo;
        this.settingsRepo = settingsRepo;
    }

    @Override
    public void onApplicationEvent(org.springframework.context.ApplicationEvent event) {
        if (event instanceof AuthenticationSuccessEvent success) {
            String username = success.getAuthentication().getName();
            Optional<User> userOpt = userRepository.findByUsername(username);
            userOpt.ifPresent(user -> {
                user.setFailedAttempts(0);
                user.setLastLoginAt(OffsetDateTime.now());
                userRepository.save(user);

                AuditEvent ae = new AuditEvent();
                ae.setEventType("LOGIN_SUCCESS");
                ae.setAppUser(username);
                auditRepo.save(ae);
            });
        } else if (event instanceof AuthenticationFailureBadCredentialsEvent failure) {
            String username = String.valueOf(failure.getAuthentication().getPrincipal());
            Optional<User> userOpt = userRepository.findByUsername(username);
            SecuritySettings st = settingsRepo.findById((short)1).orElseGet(SecuritySettings::new);
            int threshold = Math.min(st.getMaxFailedAttempts(), st.getMaxFailedAttemptsCap());

            userOpt.ifPresent(user -> {
                int newCount = user.getFailedAttempts() + 1;
                user.setFailedAttempts(newCount);
                if (newCount >= threshold) {
                    user.setLockedUntil(OffsetDateTime.now().plusMinutes(st.getLockMinutes()));
                }
                userRepository.save(user);
            });

            AuditEvent ae = new AuditEvent();
            ae.setEventType("LOGIN_FAILURE");
            ae.setAppUser(username);
            ae.setDetails(java.util.Map.of("reason", "BAD_CREDENTIALS"));
            auditRepo.save(ae);
        } else if (event instanceof AuthenticationFailureLockedEvent locked) {
            String username = String.valueOf(locked.getAuthentication().getPrincipal());
            AuditEvent ae = new AuditEvent();
            ae.setEventType("LOGIN_FAILURE");
            ae.setAppUser(username);
            ae.setDetails(java.util.Map.of("reason", "LOCKED"));
            auditRepo.save(ae);
        }
    }
}


