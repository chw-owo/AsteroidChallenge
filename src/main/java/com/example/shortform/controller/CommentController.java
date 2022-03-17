package com.example.shortform.controller;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.dto.request.CommentRequestDto;
import com.example.shortform.dto.resonse.CommentIdResponseDto;
import com.example.shortform.exception.UnauthorizedException;
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

    // 댓글 작성 API
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentIdResponseDto> writeComment(@PathVariable Long postId,
                                                             @RequestBody CommentRequestDto requestDto,
                                                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        // 로그인 한 유저만 이용가능하도록 설정
        if (principalDetails != null) {
            return commentService.writeComment(postId, requestDto, principalDetails);
        } else {
            throw new UnauthorizedException("로그인 후 이용가능합니다.");
        }
    }

    // 댓글 삭제 API
    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        // 로그인 한 유저만 이용가능하도록 설정
        if (principalDetails != null) {
            commentService.deleteComment(commentId, principalDetails);
        } else {
            throw new UnauthorizedException("로그인 후 이용가능합니다.");
        }
    }
}
