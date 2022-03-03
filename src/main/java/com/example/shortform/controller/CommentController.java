package com.example.shortform.controller;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.dto.request.CommentRequestDto;
import com.example.shortform.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> writeComment(@PathVariable Long postId,
                                          @RequestBody CommentRequestDto requestDto,
                                          @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails != null) {
            return commentService.writeComment(postId, requestDto, principalDetails);
        } else {
            throw new NullPointerException("로그인 후 이용가능합니다.");
        }
    }

    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails != null) {
            commentService.deleteComment(commentId, principalDetails);
        } else {
            throw new NullPointerException("로그인 후 이용가능합니다.");
        }
    }
}
