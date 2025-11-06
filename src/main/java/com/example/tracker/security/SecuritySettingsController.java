package com.example.tracker.security;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SecuritySettingsController {

    private final SecuritySettingsRepository repo;

    public SecuritySettingsController(SecuritySettingsRepository repo) {
        this.repo = repo;
    }

    @GetMapping({"/security/settings", "/admin/settings"})
    public String get(Model model) {
        SecuritySettings s = repo.findById((short)1).orElseGet(SecuritySettings::new);
        model.addAttribute("s", s);
        return "security/settings";
    }

    @PostMapping("/security/settings")
    public String save(
            @RequestParam int maxFailedAttempts,
            @RequestParam int lockMinutes,
            @RequestParam int sessionIdleMinutes
    ) {
        SecuritySettings s = repo.findById((short)1).orElseGet(SecuritySettings::new);
        s.setMaxFailedAttempts(Math.min(Math.max(1, maxFailedAttempts), s.getMaxFailedAttemptsCap()));
        s.setLockMinutes(Math.max(1, lockMinutes));
        s.setSessionIdleMinutes(Math.max(1, sessionIdleMinutes));
        repo.save(s);
        return "redirect:/security/settings";
    }
}


