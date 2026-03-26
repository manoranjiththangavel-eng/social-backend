package com.example.demo.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeedResponse {

    private Long postId;
    private String content;
    private String mediaUrl;
    private LocalDateTime createdAt;

    // ✅ USER INFO (UPDATED)
    private Long userId;
    private String userName;
    private String userEmail;

    private long likeCount;
    private long commentCount;

    // Getters & Setters

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // ✅ NEW USER FIELDS

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public long getLikeCount() { return likeCount; }
    public void setLikeCount(long likeCount) { this.likeCount = likeCount; }

    public long getCommentCount() { return commentCount; }
    public void setCommentCount(long commentCount) { this.commentCount = commentCount; }
}