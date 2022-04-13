package com.example.shortform.controller;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.dto.request.PostRequestDto;
import com.example.shortform.dto.resonse.*;
import com.example.shortform.exception.NotFoundException;
import com.example.shortform.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/challenge/{challengeId}/posts")
    public ResponseEntity<PostWriteResponseDto> writePost(@PathVariable Long challengeId,
                                                          @RequestPart(value = "postImage",required = false) MultipartFile multipartFile,
                                                          @RequestPart("post") PostRequestDto requestDto,
                                                          @AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {
        if (principalDetails != null) {
            return postService.writePost(challengeId, requestDto, multipartFile, principalDetails);
        } else{
            throw new NotFoundException("로그인한 유저정보가 없습니다.");
        }


    }

    @DeleteMapping("/posts/{postId}")
    public void deletePost(@PathVariable Long postId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails != null) {
            postService.deletePost(postId, principalDetails);
        } else {
            throw new NotFoundException("로그인한 유저정보가 없습니다.");
        }
    }

    @PatchMapping("/posts/{postId}")
    public ResponseEntity<PostIdResponseDto> modifyPost(@PathVariable Long postId,
                                                        @RequestPart("post") PostRequestDto requestDto,
                                                        @RequestPart(value = "postImage",required = false) MultipartFile multipartFile,
                                                        @AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {
        if (principalDetails != null) {
            return postService.modifyPost(postId, requestDto, principalDetails, multipartFile);
        } else {
            throw new NotFoundException("로그인한 유저정보가 없습니다.");
        }
    }

    @GetMapping("/challenge/{challengeId}/posts")
    public ResponseEntity<PostPageResponseDto> getListPost(@PathVariable Long challengeId,
                                                           @RequestParam("page") int page,
                                                           @RequestParam("size") int size) {
        Pageable postPageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Pageable commentPagealbe = PageRequest.of(0, 2, Sort.Direction.DESC, "createdAt");
        return postService.getListPost(challengeId, postPageable, commentPagealbe);
    }

    @GetMapping("/challenge/{challengeId}/posts/{postId}")
    public ResponseEntity<PostDetailPageResponseDto> getPost(@PathVariable Long challengeId, @PathVariable Long postId,
                                                             @RequestParam("page") int page,
                                                             @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        return postService.getPost(challengeId, postId, pageable);
    }
}
