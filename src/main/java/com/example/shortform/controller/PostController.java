package com.example.shortform.controller;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.dto.request.PostRequestDto;
import com.example.shortform.dto.resonse.PostIdResponseDto;
import com.example.shortform.dto.resonse.PostResponseDto;
import com.example.shortform.dto.resonse.PostWriteResponseDto;
import com.example.shortform.exception.NotFoundException;
import com.example.shortform.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    // 인증 게시글 작성 API
    @PostMapping("/challenge/{challengeId}/posts")
    public ResponseEntity<PostWriteResponseDto> writePost(@PathVariable Long challengeId,
                                                          @RequestPart(value = "postImage",required = false) MultipartFile multipartFile,
                                                          @RequestPart("post") PostRequestDto requestDto,
                                                          @AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException, ParseException {
        // 로그인 한 유저만 이용가능하도록 설정
        if (principalDetails != null) {
            return postService.writePost(challengeId, requestDto, multipartFile, principalDetails);
        } else{
            throw new NotFoundException("로그인한 유저정보가 없습니다.");
        }


    }

    // 인증 게시글 삭제 API
    @DeleteMapping("/posts/{postId}")
    public void deletePost(@PathVariable Long postId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails != null) {
            postService.deletePost(postId, principalDetails);
        } else {
            throw new NotFoundException("로그인한 유저정보가 없습니다.");
        }
    }

    // 인증 게시글 수정 API
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

    // 인증 게시글 및 댓글 조회 API
    @GetMapping("/challenge/{challengeId}/posts")
    public ResponseEntity<List<PostResponseDto>> getListPost(@PathVariable Long challengeId,
                                                             @Qualifier("post") @PageableDefault(size = 100, sort = "createdAt", direction = Sort.Direction.DESC) Pageable postPageable,
                                                             @Qualifier("comment") @PageableDefault(size = 100, sort = "createdAt", direction = Sort.Direction.DESC) Pageable commentPageable) {
        return postService.getListPost(challengeId, postPageable, commentPageable);
    }

    @GetMapping("/challenge/{challengeId}/posts/{postId}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable Long challengeId, @PathVariable Long postId,
                                                   @PageableDefault(size = 100, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return postService.getPost(challengeId, postId, pageable);
    }
}
