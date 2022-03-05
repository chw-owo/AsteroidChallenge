package com.example.shortform.service;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.domain.*;
import com.example.shortform.dto.request.PostRequestDto;
import com.example.shortform.dto.resonse.CommentResponseDto;
import com.example.shortform.dto.resonse.PostResponseDto;

import com.example.shortform.exception.NotFoundException;
import com.example.shortform.repository.*;
import jdk.nashorn.internal.objects.Global;

import com.example.shortform.exception.ForbiddenException;
import com.example.shortform.exception.NotFoundException;
import com.example.shortform.repository.ChallengeRepository;
import com.example.shortform.repository.CommentRepository;
import com.example.shortform.repository.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import sun.security.krb5.internal.ccache.CredentialsCache;

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final ChallengeRepository challengeRepository;
    private final CommentRepository commentRepository;
    private final ImageFileService imageFileService;
    private final UserRepository userRepository;
    private final DateCheckRepository dateCheckRepository;


    @Autowired
    public PostService(PostRepository postRepository,
                       ChallengeRepository challengeRepository,
                       CommentRepository commentRepository,
                       ImageFileService imageFileService,
                       UserRepository userRepository,
                       DateCheckRepository dateCheckRepository) {
        this.postRepository = postRepository;
        this.challengeRepository = challengeRepository;
        this.commentRepository = commentRepository;
        this.imageFileService = imageFileService;
        this.userRepository = userRepository;
        this.dateCheckRepository = dateCheckRepository;
    }

    @Transactional
    public ResponseEntity<?> writePost(Long challengeId,
                                       PostRequestDto requestDto,
                                       MultipartFile multipartFile,
                                       PrincipalDetails principalDetails) throws IOException, ParseException {

        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NotFoundException("챌린지가 존재하지 않습니다.")
        );

        //인증 게시글은 하루에 하나만==================================================

        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if(postRepository.count()>0){
            Post recentPost = postRepository.findTop1ByOrderByCreatedAtDesc();
            String recentPostTime = dateFormat.format(java.sql.Timestamp.valueOf(recentPost.getCreatedAt()));
            String nowTime = dateFormat.format(now);

            if(nowTime.equals(recentPostTime)) {
                throw new IllegalArgumentException("오늘은 이미 인증을 마쳤습니다.");
            }
        }
        //============================================================================


        Post post = postRepository.save(requestDto.toEntity(challenge, principalDetails.getUser()));

        ImageFile imageFile = imageFileService.upload(multipartFile, post);

        post.setImageFile(imageFile);
        User user = userRepository.findByEmail(principalDetails.getUsername()).orElseThrow(()-> new NotFoundException("존재하지 않는 사용자입니다"));


        user.setRankingPoint(user.getRankingPoint()+1);

        return ResponseEntity.ok(post.toResponse());
    }



    @Transactional
    public void deletePost(Long postId, PrincipalDetails principalDetails) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NotFoundException("게시글이 존재하지 않습니다.")
        );

        if (!principalDetails.getUser().getId().equals(post.getUser().getId())) {
            throw new ForbiddenException("작성자만 삭제할 수 있습니다.");
        }

        postRepository.deleteById(postId);
    }

    @Transactional
    public ResponseEntity<?> modifyPost(Long postId,
                                        PostRequestDto requestDto,
                                        PrincipalDetails principalDetails,
                                        MultipartFile multipartFile) throws IOException {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NotFoundException("인증 게시글이 존재하지 않습니다.")
        );

        ImageFile imageFile = imageFileService.upload(multipartFile, post);

        if (!principalDetails.getUser().getId().equals(post.getUser().getId())) {
            throw new ForbiddenException("작성자만 수정할 수 있습니다.");
        }

        post.update(requestDto);

        return ResponseEntity.ok(post.toResponse());
    }

    @Transactional
    public ResponseEntity<?> getListPost(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NotFoundException("챌린지가 존재하지 않습니다.")
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
