package com.example.demo.controller;

import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.LikeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.util.JwtUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    // ✅ Reads from application.properties
    @Value("${app.base-url}")
    private String appBaseUrl;

    private final String UPLOAD_DIR = "/app/uploads/";

    @PostMapping("/upload")
    public Post createPostWithMedia(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String content,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "files", required = false) MultipartFile[] files
    ) throws Exception {

        String token = JwtUtil.extractTokenFromHeader(authHeader);
        String email = JwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Path uploadPath = Paths.get(UPLOAD_DIR);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        StringBuilder mediaNames = new StringBuilder();

        if (file != null && !file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());
            mediaNames.append(fileName).append(",");
        }

        if (files != null) {
            for (MultipartFile f : files) {
                if (f == null || f.isEmpty()) continue;
                String fileName = System.currentTimeMillis() + "_" + f.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.write(filePath, f.getBytes());
                mediaNames.append(fileName).append(",");
            }
        }

        Post post = new Post();
        post.setContent(content);
        post.setCreatedAt(LocalDateTime.now());
        post.setUser(user);

        if (mediaNames.length() > 0) {
            post.setMediaUrl(mediaNames.substring(0, mediaNames.length() - 1));
        }

        Post savedPost = postRepository.save(post);
        convertMediaToFullUrl(savedPost);
        sanitizeUser(savedPost);

        return savedPost;
    }

    @GetMapping
    public List<Post> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        for (Post post : posts) {
            convertMediaToFullUrl(post);
            long likeCount = likeRepository.countByPostId(post.getId());
            post.setLikeCount(likeCount);
            sanitizeUser(post);
        }
        return posts;
    }

    @DeleteMapping("/{postId}")
    public String deletePost(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long postId) {

        String token = JwtUtil.extractTokenFromHeader(authHeader);
        String email = JwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to delete this post");
        }

        if (post.getMediaUrl() != null) {
            String[] urls = post.getMediaUrl().split(",");
            for (String url : urls) {
                if (url == null || url.isEmpty()) continue;
                try {
                    String fileName = url.substring(url.lastIndexOf("/") + 1);
                    Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName);
                    Files.deleteIfExists(filePath);
                } catch (Exception e) {
                    System.out.println("⚠️ Failed to delete file: " + url);
                }
            }
        }

        postRepository.delete(post);
        return "Post deleted";
    }

    // ✅ Uses appBaseUrl from application.properties
    private void convertMediaToFullUrl(Post post) {
        if (post.getMediaUrl() == null) return;
        String[] files = post.getMediaUrl().split(",");
        StringBuilder urls = new StringBuilder();
        for (String f : files) {
            if (!f.isEmpty()) {
                urls.append(appBaseUrl).append("/uploads/").append(f).append(",");
            }
        }
        if (urls.length() > 0) {
            post.setMediaUrl(urls.substring(0, urls.length() - 1));
        }
    }

    // ✅ Remove sensitive user data
    private void sanitizeUser(Post post) {
        if (post.getUser() != null) {
            post.getUser().setPassword(null);
        }
    }
}