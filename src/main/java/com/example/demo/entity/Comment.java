package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// ✅ REQUIRED IMPORTS
import com.example.demo.entity.User;
import com.example.demo.entity.Post;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    private LocalDateTime createdAt;

    // 🔗 Many comments → One user
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"password", "comments", "posts"}) // ✅ hide password
    private User user;

    // 🔗 Many comments → One post
    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonIgnoreProperties({"user"}) // ✅ prevent recursion
    private Post post;

    // 🕒 Auto set time
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters & Setters
    public Long getId() { return id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
}