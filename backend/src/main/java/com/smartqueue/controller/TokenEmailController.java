package com.smartqueue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/token")
public class TokenEmailController {

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/sendTokenEmail")
    public String sendTokenEmail(@RequestParam String email,
                                 @RequestParam int tokenNumber) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom("gokicbe20@gmail.com");   // ⚠️ Must match SMTP username
            message.setTo(email);
            message.setSubject("Smart Queue - Your Token");
            message.setText(
                    "Hello,\n\n" +
                    "Your token number is: " + tokenNumber + "\n" +
                    "Please wait for your turn.\n\n" +
                    "Thank you,\nSmart Queue Team"
            );

            mailSender.send(message);

            System.out.println("✅ Mail Sent Successfully to: " + email);
            return "Email sent successfully to " + email;

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Email sending failed: " + e.getMessage();
        }
    }
}
