package com.example.shortform.controller;

import com.example.shortform.dto.RequestDto.PostRequestDto;
import com.example.shortform.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/challenge/{challengeId}/posts")
    public ResponseEntity<?> writePost(@PathVariable Long challengeId, @RequestBody PostRequestDto requestDto) {
        return postService.writePost(challengeId, requestDto);
    }

    @DeleteMapping("/posts/{postId}")
    public void deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<?> modifyPost(@PathVariable Long postId, @RequestBody PostRequestDto requestDto) {
        return postService.modifyPost(postId, requestDto);
    }

    @GetMapping("/challenge/{challengeId}/posts")
    public ResponseEntity<?> getListPost(@PathVariable Long challengeId) {
        return postService.getListPost(challengeId);
    }
}
