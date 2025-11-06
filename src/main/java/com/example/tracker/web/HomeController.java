package com.example.tracker.web;

import com.example.tracker.security.AppUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/welcome"})
    public String welcome(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = false;
        if (auth != null && auth.getPrincipal() instanceof AppUserDetails aud) {
            isAdmin = aud.getUser().isAdmin() || "ADMIN".equalsIgnoreCase(aud.getUser().getRole());
        }
        model.addAttribute("isAdmin", isAdmin);
        return "welcome/index";
    }
}


