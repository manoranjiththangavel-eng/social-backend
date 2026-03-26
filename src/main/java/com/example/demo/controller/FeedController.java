package com.example.demo.controller;

import com.example.demo.dto.FeedResponse;
import com.example.demo.entity.Post;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.LikeRepository;
import com.example.demo.repository.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/feed")
public class FeedController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    private final String BASE_URL = "http://127.0.0.1:8080/uploads/";

    @GetMapping
    public List<FeedResponse> getFeed() {

        List<Post> posts = postRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList();

        List<FeedResponse> feedList = new ArrayList<>();

        for (Post post : posts) {

            FeedResponse feed = new FeedResponse();

            feed.setPostId(post.getId());
            feed.setContent(post.getContent());
            feed.setCreatedAt(post.getCreatedAt());

            // ✅ USER INFO (UPDATED)
            if (post.getUser() != null) {

                feed.setUserId(post.getUser().getId());

                String fullName = post.getUser().getFirstName() + " " +
                                  post.getUser().getLastName();

                feed.setUserName(fullName);
                feed.setUserEmail(post.getUser().getEmail());
            }

            // ✅ MEDIA URL FIX (MULTIPLE FILE SUPPORT)
            if (post.getMediaUrl() != null) {

                String[] files = post.getMediaUrl().split(",");
                StringBuilder fullUrls = new StringBuilder();

                for (String file : files) {
                    if (!file.isEmpty()) {
                        fullUrls.append(BASE_URL).append(file).append(",");
                    }
                }

                if (fullUrls.length() > 0) {
                    feed.setMediaUrl(fullUrls.substring(0, fullUrls.length() - 1));
                }
            }

            // ✅ LIKE COUNT
            feed.setLikeCount(
                    likeRepository.countByPostId(post.getId())
            );

            // ✅ COMMENT COUNT
            feed.setCommentCount(
                    commentRepository.countByPostId(post.getId())
            );

            feedList.add(feed);
        }

        return feedList;
    }
}