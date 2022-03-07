package com.example.shortform.controller;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.dto.request.PostRequestDto;
import com.example.shortform.exception.UnauthorizedException;
import com.example.shortform.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;

@RestController
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/challenge/{challengeId}/posts")
    public ResponseEntity<?> writePost(@PathVariable Long challengeId,
                                       @RequestPart(value = "postImage",required = false) MultipartFile multipartFile,
                                       @RequestPart("post") PostRequestDto requestDto,
                                       @AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException, ParseException {
        if (principalDetails != null) {
            return postService.writePost(challengeId, requestDto, multipartFile, principalDetails);
        } else{
            throw new UnauthorizedException("로그인한 유저정보가 없습니다.");
        }
    }

    @DeleteMapping("/posts/{postId}")
    public void deletePost(@PathVariable Long postId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails != null) {
            postService.deletePost(postId, principalDetails);
        } else {
            throw new UnauthorizedException("로그인한 유저정보가 없습니다.");
        }
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<?> modifyPost(@PathVariable Long postId,
                                        @RequestPart("post") PostRequestDto requestDto,
                                        @RequestPart(value = "postImage",required = false) MultipartFile multipartFile,
                                        @AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {
        if (principalDetails != null) {
            return postService.modifyPost(postId, requestDto, principalDetails, multipartFile);
        } else {
            throw new UnauthorizedException("로그인한 유저정보가 없습니다.");
        }
    }

    @GetMapping("/challenge/{challengeId}/posts")
    public ResponseEntity<?> getListPost(@PathVariable Long challengeId) {
        return postService.getListPost(challengeId);
    }
}
