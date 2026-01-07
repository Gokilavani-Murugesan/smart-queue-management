package com.smartqueue.controller;

import com.smartqueue.model.User;
import com.smartqueue.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // Registration endpoint
    @PostMapping("/register")
    @ResponseBody
    public String register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password) {

        if(userRepository.findByEmail(email) != null) {
            return "Email already registered!";
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("USER"); // default role

        userRepository.save(user);
        return "Registered Successfully!";
    }

    @GetMapping("/register")
    @ResponseBody
    public String registerPage() {
        return "Use the registration form!";
    }

    // Login endpoint
    @PostMapping("/login")
    @ResponseBody
    public String login(
            @RequestParam String email,
            @RequestParam String password) {

        User dbUser = userRepository.findByEmail(email);

        if (dbUser != null && dbUser.getPassword().equals(password)) {
            return "Login Successful! Welcome, " + dbUser.getName();
        }
        return "Invalid Credentials!";
    }

    @GetMapping("/login")
    @ResponseBody
    public String loginPage() {
        return "Use the login form!";
    }
}
