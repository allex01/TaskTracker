package com.example.tracker.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {
    @Query("select a from AuditEvent a where " +
            "a.occurredAt between :fromTs and :toTs and " +
            "(:type = '' or a.eventType = :type) and " +
            "(:user = '' or a.appUser = :user) " +
            "order by a.occurredAt desc")
    List<AuditEvent> search(
            @Param("type") String type,
            @Param("user") String user,
            @Param("fromTs") OffsetDateTime fromTs,
            @Param("toTs") OffsetDateTime toTs
    );
}


