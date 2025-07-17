package org.flexisaf.studbud.signup.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Studbud verification code");
        message.setText("Your Studbud verification code is: " + code);
        message.setFrom("mivaswaggs@gmail.com");
        mailSender.send(message);
    }
}
