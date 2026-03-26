package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*") // ✅ added for frontend access
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<User> getAllUsers() {

        List<User> users = userRepository.findAll();

        // ✅ remove passwords from all users
        for (User user : users) {
            user.setPassword(null);
        }

        return users;
    }

    @PostMapping
    public Object createUser(@RequestBody User user) {

        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser.isPresent()) {
            return "User already exists!";
        }

        return userRepository.save(user);
    }

    // 👤 GET CURRENT LOGGED-IN USER (FIXED)
    @GetMapping("/me")
    public User getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {

        // ✅ FIX 1: Handle missing header (prevents 400 error)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        try {
            // 🔐 Extract token
            String token = JwtUtil.extractTokenFromHeader(authHeader);
            String email = JwtUtil.extractEmail(token);

            // 🔍 Fetch user
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // ❌ Hide password
            user.setPassword(null);

            return user;

        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired token");
        }
    }
}