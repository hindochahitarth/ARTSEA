package com.art.artsea.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtp(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your OTP for ArtSea Login");
        message.setText("Your OTP is: " + otp + "\nThis OTP is valid for 5 minutes.");

        mailSender.send(message);
        System.out.println("OTP sent to email: " + toEmail);
    }
}
