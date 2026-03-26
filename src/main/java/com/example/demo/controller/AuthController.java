package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // ✅ BCrypt encoder — used for hashing and verifying passwords
   @Autowired
private BCryptPasswordEncoder passwordEncoder;

    // ✅ TEST API
    @GetMapping("/test")
    public String test() {
        return "OK";
    }

    // ✅ REGISTER API
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody User user) {

        Map<String, String> response = new HashMap<>();

        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            response.put("error", "Email already exists");
            return ResponseEntity.badRequest().body(response);
        }

        // ✅ Hash the password before saving to DB
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        response.put("message", "User registered successfully. Please login.");
        return ResponseEntity.ok(response);
    }

    // ✅ LOGIN API
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User loginUser) {

        Map<String, String> response = new HashMap<>();

        Optional<User> userOpt = userRepository.findByEmail(loginUser.getEmail());

        if (userOpt.isEmpty()) {
            response.put("error", "Invalid email or password");
            return ResponseEntity.status(401).body(response);
        }

        User user = userOpt.get();

        // ✅ BCrypt compare — never compare plain text passwords
        if (!passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
            response.put("error", "Invalid email or password");
            return ResponseEntity.status(401).body(response);
        }

        String token = JwtUtil.generateToken(user.getEmail());
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
}