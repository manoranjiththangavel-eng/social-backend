package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/likes")
public class LikeController {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    // ❤️ TOGGLE LIKE (LIKE / UNLIKE)
    @PostMapping
    public Map<String, Object> toggleLike(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long postId) {

        Map<String, Object> response = new HashMap<>();

        // 🔐 Extract user from JWT
        String token = JwtUtil.extractTokenFromHeader(authHeader);
        String email = JwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // 🔁 CHECK IF ALREADY LIKED
        boolean alreadyLiked = likeRepository
                .existsByUserIdAndPostId(user.getId(), postId);

        if (alreadyLiked) {
            // ❌ UNLIKE
            Like like = likeRepository.findAll().stream()
                    .filter(l -> l.getUser().getId().equals(user.getId())
                              && l.getPost().getId().equals(postId))
                    .findFirst()
                    .orElse(null);

            if (like != null) {
                likeRepository.delete(like);
            }

            response.put("message", "Post unliked");
            response.put("liked", false);

        } else {
            // ❤️ LIKE
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);

            response.put("message", "Post liked");
            response.put("liked", true);
        }

        // 🔢 UPDATED LIKE COUNT
        long likeCount = likeRepository.countByPostId(postId);
        response.put("likeCount", likeCount);

        return response;
    }

    // ❤️ GET LIKE COUNT (OPTIONAL API)
    @GetMapping("/count")
    public long getLikeCount(@RequestParam Long postId) {
        return likeRepository.countByPostId(postId);
    }
}