package com.smartqueue.controller;

import com.smartqueue.model.Token;
import com.smartqueue.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/token")
@CrossOrigin(origins = "*")  // allow frontend requests
public class TokenController {

    @Autowired
    private TokenRepository tokenRepository;

    // =========================
    // 1️⃣ Add token (per category + service + department + subservice)
    // =========================
    @PostMapping("/add")
    public Token addToken(@RequestBody Token token) {
        // Get last token number for the same category/service/department/subservice
        int lastNumber = tokenRepository
                .findTopByCategoryAndServiceAndDepartmentAndSubserviceOrderByTokenNumberDesc(
                        token.getCategory(), token.getService(), token.getDepartment(), token.getSubservice())
                .map(Token::getTokenNumber)
                .orElse(0);

        token.setTokenNumber(lastNumber + 1);
        token.setStatus("WAITING");
        token.setCreatedAt(LocalDateTime.now());
        token.setProcessingTimeMinutes(5); // default processing time per token (can be updated after completion)

        return tokenRepository.save(token);
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
    // 4️⃣ Estimated waiting time for a combination
    // =========================
    @GetMapping("/wait-time")
    public int getEstimatedWaitTime(@RequestParam String category,
                                    @RequestParam String service,
                                    @RequestParam String department,
                                    @RequestParam String subservice) {

        // Get all waiting tokens in this combination
        List<Token> waitingTokens = tokenRepository
                .findByCategoryAndServiceAndDepartmentAndSubserviceAndStatusOrderByTokenNumberAsc(
                        category, service, department, subservice, "WAITING");

        int totalWait = 0;
        for (Token t : waitingTokens) {
            totalWait += t.getProcessingTimeMinutes();
        }

        // If no waiting tokens, default 0 min
        return totalWait;
    }

    // =========================
    // 5️⃣ Mark token as DONE and update processing time
    // =========================
    @PostMapping("/complete/{id}")
    public Token completeToken(@PathVariable Long id) {
        Optional<Token> tokenOpt = tokenRepository.findById(id);
        if (tokenOpt.isPresent()) {
            Token token = tokenOpt.get();
            token.setStatus("DONE");

            // Calculate actual processing time in minutes
            Duration duration = Duration.between(token.getCreatedAt(), LocalDateTime.now());
            token.setProcessingTimeMinutes((int) Math.max(1, duration.toMinutes()));

            return tokenRepository.save(token);
        }
        return null;
    }

    // =========================
    // 6️⃣ Export tokens as CSV
    // =========================
    @GetMapping("/export/{category}")
    public void exportTokens(@PathVariable String category, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"tokens.csv\"");

        List<Token> tokens = tokenRepository.findByCategory(category);

        PrintWriter writer = response.getWriter();
        writer.println("TokenNumber,Name,Phone,Category,Service,Department,Subservice,Status,CreatedAt,ProcessingTimeMinutes");
        for (Token t : tokens) {
            writer.println(t.getTokenNumber() + "," +
                    t.getName() + "," +
                    t.getPhone() + "," +
                    t.getCategory() + "," +
                    t.getService() + "," +
                    t.getDepartment() + "," +
                    t.getSubservice() + "," +
                    t.getStatus() + "," +
                    t.getCreatedAt() + "," +
                    t.getProcessingTimeMinutes());
        }
        writer.flush();
    }
}
