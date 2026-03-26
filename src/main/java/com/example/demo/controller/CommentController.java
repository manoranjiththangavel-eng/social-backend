package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    // 💬 ADD COMMENT
    @PostMapping
    public Map<String, Object> addComment(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long postId,
            @RequestParam String text) {

        Map<String, Object> response = new HashMap<>();

        // 🔐 Extract user
        String token = JwtUtil.extractTokenFromHeader(authHeader);
        String email = JwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // 💬 Save comment
        Comment comment = new Comment();
        comment.setText(text);
        comment.setUser(user);
        comment.setPost(post);

        Comment saved = commentRepository.save(comment);

        // ✅ Clean response
        response.put("id", saved.getId());
        response.put("text", saved.getText());
        response.put("user", user.getFirstName());
        response.put("postId", postId);

        return response;
    }

    // 💬 GET COMMENTS (LATEST FIRST)
    @GetMapping
    public List<Map<String, Object>> getComments(@RequestParam Long postId) {

        List<Comment> comments =
                commentRepository.findByPostIdOrderByCreatedAtDesc(postId);

        List<Map<String, Object>> response = new ArrayList<>();

        for (Comment c : comments) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("text", c.getText());
            map.put("user", c.getUser().getFirstName());
            map.put("createdAt", c.getCreatedAt());

            response.add(map);
        }

        return response;
    }
}