package com.art.artsea.controller;

import com.art.artsea.model.User;
import com.art.artsea.repository.UserRepository;
import com.art.artsea.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/forgot-password")
public class ForgotPasswordController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Step 1: Send OTP (only if Buyer/Seller exists)
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        Optional<User> userOptional = userRepo.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "No account found with this email"
            ));
        }

        try {
            otpService.sendOtp(email); // generate and send OTP
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "OTP sent successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Failed to send OTP"
            ));
        }
    }

    // Step 2: Verify OTP (only for existing Buyer/Seller)
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        Optional<User> userOptional = userRepo.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "No account found with this email"
            ));
        }

        boolean valid = otpService.verifyOtpForForgotPassword(email, otp);
        if (!valid) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Invalid or expired OTP"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "OTP verified successfully"
        ));
    }

    // Step 3: Reset Password
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        Optional<User> userOptional = userRepo.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "User not found"
            ));
        }

        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Password reset successfully"
        ));
    }
}