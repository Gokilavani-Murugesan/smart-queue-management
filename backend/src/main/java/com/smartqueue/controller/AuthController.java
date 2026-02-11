package com.smartqueue.controller;
import com.smartqueue.model.User;
import com.smartqueue.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // ✅ REGISTER
    @PostMapping(value = "/register", consumes = "application/json")
    public Map<String, Object> register(@RequestBody User user) {

        Map<String, Object> response = new HashMap<>();

        if (userRepository.findByEmail(user.getEmail()) != null) {
            response.put("success", false);
            response.put("message", "Email already registered!");
            return response;
        }

        // Default role
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        userRepository.save(user);

        response.put("success", true);
        response.put("message", "Registered successfully!");
        return response;
    }

    // ✅ LOGIN
    @PostMapping(value = "/login", consumes = "application/json")
    public Map<String, Object> login(@RequestBody Map<String, String> data) {

        String email = data.get("email");
        String password = data.get("password");

        Map<String, Object> response = new HashMap<>();

        User dbUser = userRepository.findByEmail(email);

        if (dbUser != null && dbUser.getPassword().equals(password)) {
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("role", dbUser.getRole());
            response.put("name", dbUser.getName());
        } else {
            response.put("success", false);
            response.put("message", "Invalid credentials");
        }

        return response;
    }
}
