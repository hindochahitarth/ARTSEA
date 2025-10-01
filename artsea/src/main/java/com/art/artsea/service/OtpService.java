package com.art.artsea.service;

import com.art.artsea.model.User;
import com.art.artsea.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OtpService {

    private Map<String, OtpHolder> otpStore = new HashMap<>();

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder; //  for hashing passwords

    public String sendOtp(String email) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        OtpHolder otpHolder = new OtpHolder(otp, LocalDateTime.now());
        otpStore.put(email, otpHolder);

        emailService.sendOtp(email, otp);

        boolean isExisting = userRepo.findByEmail(email).isPresent();
        return isExisting ? "false" : "true";
    }


    public Map<String, String> verifyOtp(String email, String otp, String name, String rawPassword, String userType) {
        Map<String, String> result = new HashMap<>();
        OtpHolder holder = otpStore.get(email);

        if (holder != null && holder.getOtp().equals(otp) &&
                Duration.between(holder.getTime(), LocalDateTime.now()).toMinutes() <= 5) {

            User user = userRepo.findByEmail(email).orElseGet(() -> {
                User u = new User();
                u.setEmail(email);
                u.setUsername(name != null ? name : "user" + new Random().nextInt(10000));

                // Store hashed password
                if (rawPassword != null && !rawPassword.isEmpty()) {
                    u.setPassword(passwordEncoder.encode(rawPassword));
                } else {
                    u.setPassword(""); // fallback if no password provided
                }
                u.setIsVerified(0);

                // Set role based on userType parameter
                if ("SELLER".equalsIgnoreCase(userType)) {
                    u.setRole(User.Role.SELLER);
                } else {
                    u.setRole(User.Role.BUYER); // default to BUYER
                }

                return userRepo.save(u);
            });

            otpStore.remove(email);

            result.put("status", "OTP verified successfully");
            result.put("message", "Sign-up as " + user.getRole());
            result.put("role", user.getRole().toString());
            return result;
        }

        result.put("status", "error");
        result.put("message", "Invalid or expired OTP");
        return result;
    }

    // For Forgot Password OTP verification
    public boolean verifyOtpForForgotPassword(String email, String otp) {
        OtpHolder holder = otpStore.get(email);

        if (holder != null && holder.getOtp().equals(otp) &&
                Duration.between(holder.getTime(), LocalDateTime.now()).toMinutes() <= 5) {
            otpStore.remove(email);
            return true;
        }
        return false;
    }


    private static class OtpHolder {
        private final String otp;
        private final LocalDateTime time;

        public OtpHolder(String otp, LocalDateTime time) {
            this.otp = otp;
            this.time = time;
        }

        public String getOtp() { return otp; }
        public LocalDateTime getTime() { return time; }
    }
}