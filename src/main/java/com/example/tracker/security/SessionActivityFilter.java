package com.example.tracker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;

@Component
public class SessionActivityFilter extends OncePerRequestFilter {

    private final SecuritySettingsRepository settingsRepo;

    public SessionActivityFilter(SecuritySettingsRepository settingsRepo) {
        this.settingsRepo = settingsRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object last = session.getAttribute("_last_activity");
                OffsetDateTime now = OffsetDateTime.now();
                int idleMinutes = settingsRepo.findById((short)1).map(SecuritySettings::getSessionIdleMinutes).orElse(15);
                if (last instanceof OffsetDateTime lastTs) {
                    if (Duration.between(lastTs, now).toMinutes() >= idleMinutes) {
                        session.invalidate();
                        response.sendRedirect("/auth/login");
                        return;
                    }
                }
                session.setAttribute("_last_activity", now);
            }
        }
        filterChain.doFilter(request, response);
    }
}


