package com.example.shortform.service;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.domain.Comment;
import com.example.shortform.domain.Post;
import com.example.shortform.dto.request.CommentRequestDto;
import com.example.shortform.dto.resonse.CommentIdResponseDto;
import com.example.shortform.exception.ForbiddenException;
import com.example.shortform.exception.NotFoundException;
import com.example.shortform.exception.UnauthorizedException;
import com.example.shortform.repository.CommentRepository;
import com.example.shortform.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public ResponseEntity<CommentIdResponseDto> writeComment(Long postId, CommentRequestDto requestDto, PrincipalDetails principalDetails) {
        // postId를 사용해 DB에서 인증 게시글 조회
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NotFoundException("존재하지 않는 글입니다.")
        );

        // 댓글을 DB에 저장
        Comment comment = commentRepository.save(requestDto.toEntity(post, principalDetails.getUser()));

        return ResponseEntity.ok(comment.toPKResponse());
    }

    @Transactional
    public void deleteComment(Long commentId, PrincipalDetails principalDetails) {
        // commentID를 이용해 DB에서 댓글 조회
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("존재하지 않는 댓글입니다.")
        );

        // 로그인 한 유저와 댓글 작성자가 일치하는지 확인
        // 작성자만 삭제 가능
        if (!principalDetails.getUser().getId().equals(comment.getUser().getId())) {
            throw new ForbiddenException("작성자만 삭제할 수 있습니다.");
        }

        commentRepository.deleteById(commentId);
    }
}
