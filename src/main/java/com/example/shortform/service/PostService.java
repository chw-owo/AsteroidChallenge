package com.example.shortform.service;

import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.Comment;
import com.example.shortform.domain.Post;
import com.example.shortform.dto.RequestDto.PostRequestDto;
import com.example.shortform.dto.ResponseDto.CommentDetailResponseDto;
import com.example.shortform.dto.ResponseDto.PostResponseDto;
import com.example.shortform.repository.ChallengeRepository;
import com.example.shortform.repository.CommentRepository;
import com.example.shortform.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final ChallengeRepository challengeRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public PostService(PostRepository postRepository,
                       ChallengeRepository challengeRepository,
                       CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.challengeRepository = challengeRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public ResponseEntity<?> writePost(Long challengeId, PostRequestDto requestDto) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NullPointerException("챌린지가 존재하지 않습니다.")
        );

        Post post = postRepository.save(requestDto.toEntity(challenge));

        return ResponseEntity.ok(post.toResponse());
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("게시글이 존재하지 않습니다.")
        );

        postRepository.deleteById(postId);
    }

    @Transactional
    public ResponseEntity<?> modifyPost(Long postId, PostRequestDto requestDto) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("인증 게시글이 존재하지 않습니다.")
        );

        post.update(requestDto);

        return ResponseEntity.ok(post.toResponse());
    }

    @Transactional
    public ResponseEntity<?> getListPost(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NullPointerException("챌린지가 존재하지 않습니다.")
        );

        List<Post> postList = postRepository.findAllByChallenge(challenge);
        List<PostResponseDto> responseDtoList = new ArrayList<>();
        List<CommentDetailResponseDto> commentDetailList = new ArrayList<>();

        for (Post post : postList) {
            List<Comment> commentList = commentRepository.findAllByPost(post);
            for (Comment comment : commentList) {
                CommentDetailResponseDto commentDetailResponseDto = comment.toResponse();
                String commentCreatedAt = commentDetailResponseDto.getCreatedAt();
                String year = commentCreatedAt.substring(0,4) + "년";
                String month = commentCreatedAt.substring(5,7) + "월";
                String day = commentCreatedAt.substring(8,10) + "일";
                String time = commentCreatedAt.substring(11,19);
                commentCreatedAt = year + month + day + time;
                commentDetailList.add(commentDetailResponseDto.setCreatedAt(commentCreatedAt));
            }
            PostResponseDto postResponseDto = post.toResponse(commentDetailList);
            String postCreatedAt = postResponseDto.getCreatedAt();
            String year = postCreatedAt.substring(0,4) + "년";
            String month = postCreatedAt.substring(5,7) + "월";
            String day = postCreatedAt.substring(8,10) + "일";
            String time = postCreatedAt.substring(11,19);
            postCreatedAt = year + month + day + time;
            responseDtoList.add(postResponseDto.setCreatedAt(postCreatedAt));
        }

        return ResponseEntity.ok(responseDtoList);
    }
}
