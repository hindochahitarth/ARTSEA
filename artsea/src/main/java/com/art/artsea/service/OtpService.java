package com.art.artsea.service;

import com.art.artsea.model.User;
import com.art.artsea.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public String sendOtp(String email) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        OtpHolder otpHolder = new OtpHolder(otp, LocalDateTime.now());
        otpStore.put(email, otpHolder);

        emailService.sendOtp(email, otp);

        boolean isExisting = userRepo.findByEmail(email).isPresent();
        return isExisting ? "false" : "true";
    }

    public Map<String, String> verifyOtp(String email, String otp, HttpSession session, String name) {
        Map<String, String> result = new HashMap<>();
        OtpHolder holder = otpStore.get(email);

        if (holder != null && holder.getOtp().equals(otp) &&
                Duration.between(holder.getTime(), LocalDateTime.now()).toMinutes() <= 5) {

            // Now create or fetch user
            User user = userRepo.findByEmail(email).orElseGet(() -> {
                User u = new User();
                u.setEmail(email);
                u.setUsername(name != null ? name : "user" + new Random().nextInt(10000));
                u.setPassword(""); // not used yet
                u.setRole(User.Role.BUYER);
                return userRepo.save(u);
            });

            session.setAttribute("user", user);
            otpStore.remove(email); // cleanup
            result.put("status", "OTP verified successfully");
            result.put("message", "Logged in as " + user.getRole());
            return result;
        }

        result.put("status", "OTP verification failed");
        result.put("message", "Invalid or expired OTP");
        return result;
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
