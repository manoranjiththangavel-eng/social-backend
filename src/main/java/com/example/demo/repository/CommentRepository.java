package com.example.demo.repository;

import com.example.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 💬 Get all comments for a post (ordered by latest first)
    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);

    // 🔢 Count comments
    long countByPostId(Long postId);
}