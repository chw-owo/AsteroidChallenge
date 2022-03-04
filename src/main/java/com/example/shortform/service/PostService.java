package com.example.shortform.service;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.Comment;
import com.example.shortform.domain.ImageFile;
import com.example.shortform.domain.Post;
import com.example.shortform.dto.request.PostRequestDto;
import com.example.shortform.dto.resonse.CommentResponseDto;
import com.example.shortform.dto.resonse.PostResponseDto;
import com.example.shortform.repository.ChallengeRepository;
import com.example.shortform.repository.CommentRepository;
import com.example.shortform.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final ChallengeRepository challengeRepository;
    private final CommentRepository commentRepository;
    private final ImageFileService imageFileService;

    @Autowired
    public PostService(PostRepository postRepository,
                       ChallengeRepository challengeRepository,
                       CommentRepository commentRepository,
                       ImageFileService imageFileService) {
        this.postRepository = postRepository;
        this.challengeRepository = challengeRepository;
        this.commentRepository = commentRepository;
        this.imageFileService = imageFileService;
    }

    @Transactional
    public ResponseEntity<?> writePost(Long challengeId,
                                       PostRequestDto requestDto,
                                       MultipartFile multipartFile,
                                       PrincipalDetails principalDetails) throws IOException {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NullPointerException("챌린지가 존재하지 않습니다.")
        );

        Post post = postRepository.save(requestDto.toEntity(challenge, principalDetails.getUser()));

        ImageFile imageFile = imageFileService.upload(multipartFile, post);

        post.setImageFile(imageFile);

        return ResponseEntity.ok(post.toResponse());
    }

    @Transactional
    public void deletePost(Long postId, PrincipalDetails principalDetails) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("게시글이 존재하지 않습니다.")
        );

        if (!principalDetails.getUser().getId().equals(post.getUser().getId())) {
            throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
        }

        postRepository.deleteById(postId);
    }

    @Transactional
    public ResponseEntity<?> modifyPost(Long postId,
                                        PostRequestDto requestDto,
                                        PrincipalDetails principalDetails,
                                        MultipartFile multipartFile) throws IOException {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("인증 게시글이 존재하지 않습니다.")
        );

        if (multipartFile != null) {
            ImageFile imageFile = imageFileService.upload(multipartFile, post);
        }

        if (!principalDetails.getUser().getId().equals(post.getUser().getId())) {
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }

        post.update(requestDto);

        return ResponseEntity.ok(post.toResponse());
    }

    @Transactional
    public ResponseEntity<?> getListPost(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NullPointerException("챌린지가 존재하지 않습니다.")
        );

        List<Post> postList = postRepository.findAllByChallengeId(challengeId);
        List<PostResponseDto> responseDtoList = new ArrayList<>();


        for (Post post : postList) {
            List<CommentResponseDto> commentDetailList = new ArrayList<>();
            List<Comment> commentList = commentRepository.findAllByPostId(post.getId());
            for (Comment comment : commentList) {
                CommentResponseDto commentDetailResponseDto = comment.toResponse();
                String commentCreatedAt = comment.getCreatedAt().toString();
                String year = commentCreatedAt.substring(0,4) + "년";
                String month = commentCreatedAt.substring(5,7) + "월";
                String day = commentCreatedAt.substring(8,10) + "일";
                String time = commentCreatedAt.substring(11,19);
                commentCreatedAt = year + month + day + time;
                commentDetailResponseDto.setCreatedAt(commentCreatedAt);
                commentDetailList.add(commentDetailResponseDto);
            }
            PostResponseDto postResponseDto = post.toResponse(commentDetailList);
            String postCreatedAt = post.getCreatedAt().toString();
            String year = postCreatedAt.substring(0,4) + "년";
            String month = postCreatedAt.substring(5,7) + "월";
            String day = postCreatedAt.substring(8,10) + "일";
            String time = postCreatedAt.substring(11,19);
            postCreatedAt = year + month + day + time;
            postResponseDto.setCreatedAt(postCreatedAt);
            responseDtoList.add(postResponseDto);
        }

        return ResponseEntity.ok(responseDtoList);
    }
}
