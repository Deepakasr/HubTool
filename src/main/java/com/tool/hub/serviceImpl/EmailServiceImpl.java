package com.tool.hub.serviceImpl;

import com.tool.hub.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOtp(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("dk88107765@gmail.com"); // add here
        
        message.setTo(email);

        message.setSubject("ToolHub Email Verification");

        message.setText("Your OTP is : " + otp + "\nValid for 5 minutes.");

        mailSender.send(message);
    }
}
