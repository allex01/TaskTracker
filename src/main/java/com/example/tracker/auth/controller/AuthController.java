package com.example.tracker.auth.controller;

import com.example.tracker.user.entity.User;
import com.example.tracker.user.repo.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import com.example.tracker.auth.controller.PasswordHelperService.*;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/auth/register")
    public String registerPage() {
        return "auth/register";
    }

    @GetMapping("/auth/login")
    public String loginPage() {
        return "auth/login";
    }

    // HTMX: превью пароля — вы реализуете алгоритм позже. Сейчас — заглушка
    @PostMapping(value = "/api/auth/password/preview")
    public String passwordPreview(@RequestParam String username, Model model) {
        boolean available = StringUtils.hasText(username) && !userRepository.existsByUsername(username);
        String generatedPassword = PasswordHelperService.generatePassword(username);
        model.addAttribute("username", username);
        model.addAttribute("generatedPassword", generatedPassword);
        model.addAttribute("available", available);
        return "auth/fragments :: preview";
    }

    @PostMapping("/api/auth/register")
    @ResponseBody
    public Map<String, Object> register(@RequestParam String username, @RequestParam String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return Map.of("ok", false, "message", "username/password пусты");
        }
        if (userRepository.existsByUsername(username)) {
            return Map.of("ok", false, "message", "username занят");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // без шифрования по запросу пользователя
        userRepository.save(user);
        return Map.of("ok", true, "message", "Пользователь зарегистрирован");
    }

    
}
