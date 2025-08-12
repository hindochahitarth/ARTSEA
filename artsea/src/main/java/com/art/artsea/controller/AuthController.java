package com.art.artsea.controller;

import com.art.artsea.model.User;
import com.art.artsea.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
@RestController
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Map<String, String> login(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false, defaultValue = "BUYER") String userType,
            HttpSession session) {

        Map<String, String> result = new HashMap<>();

        return userRepo.findByEmail(email).map(user -> {
            // First verify password
            if (!passwordEncoder.matches(password, user.getPassword())) {
                result.put("status", "error");
                result.put("message", "Invalid password");
                return result;
            }

            // Then verify role if specified
            if (userType != null && !user.getRole().toString().equalsIgnoreCase(userType)) {
                result.put("status", "error");
                result.put("message", "This account is not registered as a " + userType);
                return result;
            }

            // Successful login
            session.setAttribute("user", user);
            result.put("status", "success");
            result.put("message", "Login successful");
            result.put("role", user.getRole().toString());
            return result;

        }).orElseGet(() -> {
            result.put("status", "error");
            result.put("message", "Account not found");
            return result;
        });
    }
}
