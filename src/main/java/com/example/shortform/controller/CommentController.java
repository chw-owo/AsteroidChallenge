package com.example.shortform.controller;

import com.example.shortform.dto.RequestDto.CommentRequestDto;
import com.example.shortform.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> writeComment(@PathVariable Long postId, @RequestBody CommentRequestDto requestDto) {
        return commentService.writeComment(postId, requestDto);
    }

    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }
}
