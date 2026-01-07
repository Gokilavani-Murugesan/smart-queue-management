package com.smartqueue.controller;

import com.smartqueue.model.Token;
import com.smartqueue.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*") // Allow frontend to access
public class DashboardController {

    @Autowired
    private TokenRepository tokenRepository;

    // Get all tokens (for dashboard display)
    @GetMapping("/tokens")
    public List<Token> getAllTokens() {
        return tokenRepository.findAll();
    }

    // Get token count grouped by service
    @GetMapping("/counts")
    public Map<String, Long> getTokenCountsByService() {
        List<Token> tokens = tokenRepository.findAll();
        Map<String, Long> counts = new HashMap<>();

        for (Token token : tokens) {
            String service = token.getService();
            counts.put(service, counts.getOrDefault(service, 0L) + 1);
        }

        return counts;
    }
}
