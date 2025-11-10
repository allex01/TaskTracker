package com.example.tracker.security;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.OffsetDateTime;
import java.util.List;

@Controller
public class AuditController {
    private final AuditEventRepository auditRepo;

    public AuditController(AuditEventRepository auditRepo) {
        this.auditRepo = auditRepo;
    }

    @GetMapping("/audit")
    public String list(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            Model model
    ) {
        // Нормализуем даты, чтобы не передавать null
        OffsetDateTime fromEff = from != null ? from : OffsetDateTime.parse("0001-01-01T00:00:00Z");
        OffsetDateTime toEff = to != null ? to : OffsetDateTime.parse("9999-12-31T23:59:59Z");
        String typeEff = type != null ? type : "";
        String userEff = user != null ? user : "";
        List<AuditEvent> events = auditRepo.search(typeEff, userEff, fromEff, toEff);
        model.addAttribute("events", events);
        model.addAttribute("type", type);
        model.addAttribute("user", user);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        return "audit/list";
    }
}


