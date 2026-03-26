package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "likes",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "post_id"}))
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Many likes → One user
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 🔗 Many likes → One post
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    // Getters & Setters
    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
}