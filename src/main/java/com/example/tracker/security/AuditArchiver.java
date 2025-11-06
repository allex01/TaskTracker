package com.example.tracker.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
public class AuditArchiver {

    private final AuditEventRepository auditRepo;
    private final ObjectMapper objectMapper;

    @Value("${audit.archive.dir:./audit-archive}")
    private String archiveDir;

    @Value("${audit.archive.older-than-days:7}")
    private int olderThanDays;

    public AuditArchiver(AuditEventRepository auditRepo, ObjectMapper objectMapper) {
        this.auditRepo = auditRepo;
        this.objectMapper = objectMapper;
    }

    @Scheduled(cron = "${audit.archive.schedule-cron:0 0 * * * *}")
    public void archiveOldEvents() throws IOException {
        OffsetDateTime threshold = OffsetDateTime.now().minusDays(olderThanDays);
        List<AuditEvent> old = auditRepo.findByOccurredAtBefore(threshold);
        if (old.isEmpty()) {
            return;
        }

        Path dir = Path.of(archiveDir);
        Files.createDirectories(dir);
        String ts = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path file = dir.resolve("audit_" + ts + "_until_" + threshold.toLocalDate().toString() + ".jsonl");

        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (AuditEvent e : old) {
                String json = objectMapper.writeValueAsString(Map.of(
                    "occurredAt", String.valueOf(e.getOccurredAt()),
                    "eventType", e.getEventType(),
                    "appUser", e.getAppUser(),
                    "osUser", e.getOsUser(),
                    "workstation", e.getWorkstation(),
                    "details", e.getDetails()
                ));
                writer.write(json);
                writer.newLine();
            }
        }

        auditRepo.deleteAll(old);
    }
}


