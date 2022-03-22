package com.example.shortform.service;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.domain.*;
import com.example.shortform.dto.request.PostRequestDto;
import com.example.shortform.dto.resonse.CommentResponseDto;
import com.example.shortform.dto.resonse.PostIdResponseDto;
import com.example.shortform.dto.resonse.PostResponseDto;
import com.example.shortform.dto.resonse.PostWriteResponseDto;
import com.example.shortform.exception.ForbiddenException;
import com.example.shortform.exception.InvalidException;
import com.example.shortform.exception.NotFoundException;
import com.example.shortform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final ChallengeRepository challengeRepository;
    private final CommentRepository commentRepository;
    private final ImageFileService imageFileService;
    private final UserRepository userRepository;
    private final LevelService levelService;
    private final DateCheckRepository dateCheckRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final AuthChallengeRepository authChallengeRepository;


    @Autowired
    public PostService(PostRepository postRepository,
                       ChallengeRepository challengeRepository,
                       CommentRepository commentRepository,
                       ImageFileService imageFileService,
                       UserRepository userRepository,
                       DateCheckRepository dateCheckRepository,
                       UserChallengeRepository userChallengeRepository,
                       LevelService levelService,
                       AuthChallengeRepository authChallengeRepository
) {
        this.postRepository = postRepository;
        this.challengeRepository = challengeRepository;
        this.commentRepository = commentRepository;
        this.imageFileService = imageFileService;
        this.userRepository = userRepository;
        this.dateCheckRepository = dateCheckRepository;
        this.userChallengeRepository = userChallengeRepository;

        this.authChallengeRepository = authChallengeRepository;
        this.levelService = levelService;
    }

    @Transactional
    public ResponseEntity<PostWriteResponseDto> writePost(Long challengeId,
                                       PostRequestDto requestDto,
                                       MultipartFile multipartFile,
                                       PrincipalDetails principalDetails) throws IOException, ParseException {

        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NotFoundException("챌린지가 존재하지 않습니다.")
        );

        // TODO 멤버 아닌경우 인증게시글 못올리도록 수정
        UserChallenge userChallenge = userChallengeRepository.findByUserIdAndChallengeId(principalDetails.getUser().getId(), challengeId);
        if(!challenge.getMemberList().contains(userChallenge)){
            throw new ForbiddenException("챌린지에 가입한 사람만 작성할 수 있습니다.");
        }

        //Write a Post per a day
        LocalDate now = LocalDate.now();
        LocalDateTime today = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0, 0);

        if(!postRepository.existsByUserAndChallengeIdAndCreatedAtBetween(principalDetails.getUser(), challengeId, today, today.plusDays(1))){
            // 해당 게시글에 인증하면 당일 인증여부 체크
            userChallenge.setDailyAuthenticated(true);
            userChallenge.setAuthCount(userChallenge.getAuthCount() + 1);
        } else {
            throw new InvalidException("인증은 하루에 1회만 가능합니다.");
        }

        // update percentage of report - plus authmember
        // 리포트 퍼센테이지 업데이트 - 인증 멤버 ++
        AuthChallenge authChallenge = authChallengeRepository.findByChallengeAndDate(challenge, now);
        authChallenge.setAuthMember(authChallenge.getAuthMember()+1);
        authChallengeRepository.save(authChallenge);


        Post post = postRepository.save(requestDto.toEntity(challenge, principalDetails.getUser()));
        ImageFile imageFile = imageFileService.upload(multipartFile, post);
        post.setImageFile(imageFile);
        User user = userRepository.findByEmail(principalDetails.getUsername()).orElseThrow(()-> new NotFoundException("존재하지 않는 사용자입니다"));
        user.setRankingPoint(user.getRankingPoint()+1);

        // level
        boolean isLevelUp = levelService.checkLevelPoint(user);

        PostWriteResponseDto responseDto = PostWriteResponseDto.builder()
                .postId(post.getId())
                .isLevelUp(isLevelUp)
                .levelName(user.getLevel().getName())
                .build();

        return ResponseEntity.ok(responseDto);
    }



    @Transactional
    public void deletePost(Long postId, PrincipalDetails principalDetails) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NotFoundException("게시글이 존재하지 않습니다.")
        );

        if (!principalDetails.getUser().getId().equals(post.getUser().getId())) {
            throw new ForbiddenException("작성자만 삭제할 수 있습니다.");
        }
        User user = userRepository.findByEmail(principalDetails.getUsername()).orElseThrow(()-> new NotFoundException("존재하지 않는 사용자입니다"));
        user.setRankingPoint(user.getRankingPoint()-1);

        // 레벨업, 다운 로직
        levelService.checkLevelPoint(user);

        postRepository.deleteById(postId);
        // // 해당 게시글에 인증삭제하면 당일 인증여부 체크
        UserChallenge userChallenge = userChallengeRepository.findByUserIdAndChallengeId(principalDetails.getUser().getId(), post.getChallenge().getId());
        userChallenge.setDailyAuthenticated(false);
        userChallenge.setAuthCount(userChallenge.getAuthCount() - 1);

        //for Report, 퍼센테이지 업데이트====================================================

        AuthChallenge authChallenge = authChallengeRepository.findByChallengeAndDate(userChallenge.getChallenge(), post.getCreatedAt().toLocalDate());
        authChallenge.setAuthMember(authChallenge.getAuthMember()-1);
        authChallengeRepository.save(authChallenge);

        //============================================================================


    }

    @Transactional
    public ResponseEntity<PostIdResponseDto> modifyPost(Long postId,
                                                        PostRequestDto requestDto,
                                                        PrincipalDetails principalDetails,
                                                        MultipartFile multipartFile) throws IOException {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NotFoundException("인증 게시글이 존재하지 않습니다.")
        );

        if (!principalDetails.getUser().getId().equals(post.getUser().getId())) {
            throw new ForbiddenException("작성자만 수정할 수 있습니다.");
        }

        if (multipartFile != null)
            imageFileService.upload(multipartFile, post);

        post.update(requestDto);

        return ResponseEntity.ok(post.toResponse());
    }

    @Transactional
    public ResponseEntity<List<PostResponseDto>> getListPost(Long challengeId, Pageable postPageable, Pageable commentPageable) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NotFoundException("챌린지가 존재하지 않습니다.")
        );

//        List<Post> postList = postRepository.findAllByChallengeIdOrderByCreatedAtDesc(challengeId);
        // DB에서 챌린지의 모든 인증게시글 조회
        Page<Post> postPage = postRepository.findAllByChallengeId(challengeId, postPageable);
        List<PostResponseDto> responseDtoList = new ArrayList<>();


        for (Post post : postPage) {
            List<CommentResponseDto> commentDetailList = new ArrayList<>();
//            List<Comment> commentList = commentRepository.findAllByPostIdOrderByCreatedAtDesc(post.getId());
            // DB에서 인증 게시글의 모든 댓글 조회
            Page<Comment> commentPage = commentRepository.findAllByPostId(post.getId(), commentPageable);
            for (Comment comment : commentPage) {
                // 댓글 날짜 형식 변경
                CommentResponseDto commentDetailResponseDto = comment.toResponse();
                String commentCreatedAt = comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
//                String commentCreatedAt = comment.getCreatedAt().toString();
//                String year = commentCreatedAt.substring(0,4) + ".";
//                String month = commentCreatedAt.substring(5,7) + ".";
//                String day = commentCreatedAt.substring(8,10) + " ";
//                String time = commentCreatedAt.substring(11,19);
//                commentCreatedAt = year + month + day + time;
                commentDetailResponseDto.setCreatedAt(commentCreatedAt);
                commentDetailList.add(commentDetailResponseDto);
            }
            // 인증 게시글 날짜 형식 변경
            PostResponseDto postResponseDto = post.toResponse(commentDetailList);
            String postCreatedAt = post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
//            String postCreatedAt = post.getCreatedAt().toString();
//            String year = postCreatedAt.substring(0,4) + ".";
//            String month = postCreatedAt.substring(5,7) + ".";
//            String day = postCreatedAt.substring(8,10) + " ";
//            String time = postCreatedAt.substring(11,19);
//            postCreatedAt = year + month + day + time;
            postResponseDto.setCreatedAt(postCreatedAt);
            responseDtoList.add(postResponseDto);
        }

        return ResponseEntity.ok(responseDtoList);
    }

    public ResponseEntity<PostResponseDto> getPost(Long challengeId, Long postId, Pageable pageable) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NotFoundException("인증 게시글이 존재하지 않습니다.")
        );

        Page<Comment> commentPage = commentRepository.findAllByPostId(postId, pageable);
        List<CommentResponseDto> commentDetailList = new ArrayList<>();

        for (Comment comment : commentPage) {
            // 댓글 날짜 형식 변경
            CommentResponseDto commentDetailResponseDto = comment.toResponse();
            String commentCreatedAt = comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
            commentDetailResponseDto.setCreatedAt(commentCreatedAt);
            commentDetailList.add(commentDetailResponseDto);
        }
        PostResponseDto postResponseDto = post.toResponse(commentDetailList);
        return ResponseEntity.ok(postResponseDto);
    }
}
