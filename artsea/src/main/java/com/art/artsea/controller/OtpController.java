package com.art.artsea.controller;

import com.art.artsea.service.OtpService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequestMapping("/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/send")
    public Map<String, String> sendOtp(@RequestParam String email) {
        String isNewUser = otpService.sendOtp(email);
        return Map.of("status", "OTP sent", "isNewUser", isNewUser);
    }

//    @PostMapping("/verify")
//    public Map<String, String> verifyOtp(@RequestParam String email, @RequestParam String otp, HttpSession session) {
//        return otpService.verifyOtp(email, otp, session);
//    }

    @GetMapping("/session")
    public String checkSession(HttpSession session) {
        return session.getAttribute("user") != null ? "User is logged in" : "User is not logged in";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "Logged out successfully";
    }
    @PostMapping("/verify")
    public Map<String, String> verifyOtp(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String password,
            @RequestParam(required = false, defaultValue = "BUYER") String userType,
            HttpSession session) {
        return otpService.verifyOtp(email, otp, name, password, userType);
    }

//    @PostMapping("/otp/verify")
//    public Map<String, String> verifyOtp(
//            @RequestParam String email,
//            @RequestParam String otp,
//            @RequestParam(required = false) String name,
//            @RequestParam(required = false) String password, //  added
//            HttpSession session) {
//
//        return otpService.verifyOtp(email, otp, session, name, password); //  now matches method
//    }


}
