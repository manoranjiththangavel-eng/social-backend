package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "posts")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private String mediaUrl;

    private LocalDateTime createdAt;

    // ✅ LINK POST TO USER (FIXED JSON LOOP)
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"posts"})
    private User user;

    // ✅ CASCADE DELETE FOR COMMENTS
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Comment> comments;

    // ✅ CASCADE DELETE FOR LIKES
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Like> likes;

    // ✅ EXISTING (NO CHANGE)
    @Transient
    private long likeCount;

    // ✅ AUTO SET CREATED TIME
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // Getter & Setter

    public Long getId() { return id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public long getLikeCount() { return likeCount; }
    public void setLikeCount(long likeCount) { this.likeCount = likeCount; }
}