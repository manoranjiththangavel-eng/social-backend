package com.example.demo.repository;

import com.example.demo.entity.Like;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    // ✅ Check if user already liked a post
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    // ✅ Count likes for a post
    long countByPostId(Long postId);

    // ✅ REQUIRED for toggle like (VERY IMPORTANT)
    Optional<Like> findByUserAndPost(User user, Post post);
}