package com.smartqueue.controller;

import com.smartqueue.model.Token;
import com.smartqueue.repository.TokenRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/token")
@CrossOrigin(origins = "*")
public class TokenController {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    // =========================
    // 1️⃣ Add token + Send Email
    // =========================
    @PostMapping("/add")
    public Token addToken(@RequestBody Token token) {

        int lastNumber = tokenRepository
                .findTopByCategoryAndServiceAndDepartmentAndSubserviceOrderByTokenNumberDesc(
                        token.getCategory(),
                        token.getService(),
                        token.getDepartment(),
                        token.getSubservice())
                .map(Token::getTokenNumber)
                .orElse(0);

        token.setTokenNumber(lastNumber + 1);
        token.setStatus("WAITING");
        token.setCreatedAt(LocalDateTime.now());
        token.setProcessingTimeMinutes(5);

        Token savedToken = tokenRepository.save(token);

        // Send email
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(savedToken.getEmail());
            message.setSubject("Smart Queue - Token Confirmation");
            message.setText(
                    "Hello " + savedToken.getName() + ",\n\n" +
                            "Your token number is: " + savedToken.getTokenNumber() + "\n" +
                            "Category: " + savedToken.getCategory() + "\n" +
                            "Service: " + savedToken.getService() + "\n\n" +
                            "Please wait for your turn.\n\n" +
                            "Thank you,\nSmart Queue System"
            );

            mailSender.send(message);
            System.out.println("✅ Email sent successfully");

        } catch (Exception e) {
            System.out.println("❌ Email sending failed: " + e.getMessage());
        }

        return savedToken;
    }

    // =========================
    // 2️⃣ Get all tokens
    // =========================
    @GetMapping("/all")
    public List<Token> getAllTokens() {
        return tokenRepository.findAll();
    }

    // =========================
    // 3️⃣ Get tokens by category
    // =========================
    @GetMapping("/category/{category}")
    public List<Token> getTokensByCategory(@PathVariable String category) {
        return tokenRepository.findByCategory(category);
    }

    // =========================
    // 4️⃣ Estimated waiting time
    // =========================
    @GetMapping("/wait-time")
    public int getEstimatedWaitTime(@RequestParam String category,
                                    @RequestParam String service,
                                    @RequestParam String department,
                                    @RequestParam String subservice) {

        List<Token> waitingTokens = tokenRepository
                .findByCategoryAndServiceAndDepartmentAndSubserviceAndStatusOrderByTokenNumberAsc(
                        category, service, department, subservice, "WAITING");

        return waitingTokens.stream()
                .mapToInt(Token::getProcessingTimeMinutes)
                .sum();
    }

    // =========================
    // 5️⃣ Mark token DONE
    // =========================
    @PostMapping("/complete/{id}")
    public Token completeToken(@PathVariable Long id) {
        Optional<Token> tokenOpt = tokenRepository.findById(id);
        if (tokenOpt.isPresent()) {
            Token token = tokenOpt.get();
            token.setStatus("DONE");

            Duration duration = Duration.between(token.getCreatedAt(), LocalDateTime.now());
            token.setProcessingTimeMinutes((int) Math.max(1, duration.toMinutes()));

            return tokenRepository.save(token);
        }
        return null;
    }

    // =========================
    // 6️⃣ Export CSV
    // =========================
    @GetMapping("/export/{category}")
    public void exportTokens(@PathVariable String category, HttpServletResponse response) throws IOException {

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"tokens.csv\"");

        List<Token> tokens = tokenRepository.findByCategory(category);
        PrintWriter writer = response.getWriter();

        writer.println("TokenNumber,Name,Email,Phone,Category,Service,Department,Subservice,Status,CreatedAt,ProcessingTimeMinutes");

        for (Token t : tokens) {
            writer.println(
                    t.getTokenNumber() + "," +
                            t.getName() + "," +
                            t.getEmail() + "," +
                            t.getPhone() + "," +
                            t.getCategory() + "," +
                            t.getService() + "," +
                            t.getDepartment() + "," +
                            t.getSubservice() + "," +
                            t.getStatus() + "," +
                            t.getCreatedAt() + "," +
                            t.getProcessingTimeMinutes()
            );
        }

        writer.flush();
    }

    // =========================
    // 7️⃣ NEW: Get token history for user (by email)
    // =========================
    @GetMapping("/user/history")
    public List<Token> getUserHistory(@RequestParam String email) {
        // ✅ Return all tokens of this user, newest first
        return tokenRepository.findByEmailOrderByCreatedAtDesc(email);
    }

}
