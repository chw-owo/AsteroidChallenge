package com.example.shortform.service;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.domain.*;
import com.example.shortform.dto.request.PostRequestDto;
import com.example.shortform.dto.resonse.*;
import com.example.shortform.exception.ForbiddenException;
import com.example.shortform.exception.InvalidException;
import com.example.shortform.exception.NotFoundException;
import com.example.shortform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;

import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private final NoticeRepository noticeRepository;


    @Autowired
    public PostService(PostRepository postRepository,
                       ChallengeRepository challengeRepository,
                       CommentRepository commentRepository,
                       ImageFileService imageFileService,
                       UserRepository userRepository,
                       DateCheckRepository dateCheckRepository,
                       UserChallengeRepository userChallengeRepository,
                       LevelService levelService,
                       AuthChallengeRepository authChallengeRepository,
                       NoticeRepository noticeRepository) {
        this.postRepository = postRepository;
        this.challengeRepository = challengeRepository;
        this.commentRepository = commentRepository;
        this.imageFileService = imageFileService;
        this.userRepository = userRepository;
        this.dateCheckRepository = dateCheckRepository;
        this.userChallengeRepository = userChallengeRepository;

        this.authChallengeRepository = authChallengeRepository;
        this.levelService = levelService;
        this.noticeRepository = noticeRepository;
    }

    @Transactional
    public ResponseEntity<PostWriteResponseDto> writePost(Long challengeId,
                                       PostRequestDto requestDto,
                                       MultipartFile multipartFile,
                                       PrincipalDetails principalDetails) throws IOException {

        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NotFoundException("챌린지가 존재하지 않습니다.")
        );

        UserChallenge userChallenge = userChallengeRepository.findByUserIdAndChallengeId(principalDetails.getUser().getId(), challengeId);
        if(!challenge.getMemberList().contains(userChallenge)){
            throw new ForbiddenException("챌린지에 가입한 사람만 작성할 수 있습니다.");
        }

        LocalDate now = LocalDate.now();
        LocalDateTime today = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0, 0);

        if(!postRepository.existsByUserAndChallengeIdAndCreatedAtBetween(principalDetails.getUser(), challengeId, today, today.plusDays(1))){
            userChallenge.setDailyAuthenticated(true);
            userChallenge.setAuthCount(userChallenge.getAuthCount() + 1);
        } else {
            throw new InvalidException("인증은 하루에 1회만 가능합니다.");
        }

        AuthChallenge authChallenge = authChallengeRepository.findByChallengeAndDate(challenge, now);
        authChallenge.setAuthMember(authChallenge.getAuthMember()+1);
        authChallengeRepository.save(authChallenge);


        Post post = postRepository.save(requestDto.toEntity(challenge, principalDetails.getUser()));
        ImageFile imageFile = imageFileService.upload(multipartFile, post);
        post.setImageFile(imageFile);
        User user = userRepository.findByEmail(principalDetails.getUsername()).orElseThrow(()-> new NotFoundException("존재하지 않는 사용자입니다"));
        user.setRankingPoint(user.getRankingPoint()+1);

        boolean isLevelUp = levelService.checkLevelPoint(user);

        Notice notice = new Notice(user, challenge, post, 1);
        noticeRepository.save(notice);

        if (challenge.getEndDate().equals(today.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")))) {
            Notice recommendNotice = new Notice(user);
            noticeRepository.save(recommendNotice);
        }



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

        levelService.checkLevelPoint(user);

        if (noticeRepository.existsByPostIdAndUserId(postId, user.getId())) {
            Notice notice = noticeRepository.findByPostIdAndUserId(postId, user.getId());
            notice.setNoticeType(Notice.NoticeType.RECORD);
        }

        postRepository.deleteById(postId);
        UserChallenge userChallenge = userChallengeRepository.findByUserIdAndChallengeId(principalDetails.getUser().getId(), post.getChallenge().getId());

        LocalDate now = LocalDate.now();

        if (now.isEqual(post.getCreatedAt().toLocalDate()))
            userChallenge.setDailyAuthenticated(false);

        userChallenge.setAuthCount(userChallenge.getAuthCount() - 1);

        AuthChallenge authChallenge = authChallengeRepository.findByChallengeAndDate(userChallenge.getChallenge(), post.getCreatedAt().toLocalDate());
        authChallenge.setAuthMember(authChallenge.getAuthMember()-1);
        authChallengeRepository.save(authChallenge);

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
    public ResponseEntity<PostPageResponseDto> getListPost(Long challengeId, Pageable postPageable, Pageable commentPageable) {
        Challenge challenge = challengeRepository.findCheckChallenge(challengeId).orElseThrow(
                () -> new NotFoundException("챌린지가 존재하지 않습니다.")
        );

        Page<Post> postPage = postRepository.findAllByChallengeId(challengeId, postPageable);
        List<PostResponseDto> responseDtoList = new ArrayList<>();

        for (Post post : postPage) {
            List<CommentResponseDto> commentDetailList = new ArrayList<>();
            Page<Comment> commentPage = commentRepository.findAllByPostId(post.getId(), commentPageable);
            for (Comment comment : commentPage) {
                CommentResponseDto commentDetailResponseDto = comment.toResponse();
                String commentCreatedAt = comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
                commentDetailResponseDto.setCreatedAt(commentCreatedAt);
                commentDetailList.add(commentDetailResponseDto);
            }
            PostResponseDto postResponseDto = post.toResponse(commentDetailList, commentPage.getTotalElements());
            String postCreatedAt = post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
            postResponseDto.setCreatedAt(postCreatedAt);
            responseDtoList.add(postResponseDto);
        }
        PostPageResponseDto postPageResponseDto = PostPageResponseDto.builder()
                .postList(responseDtoList)
                .next(postPage.hasNext())
                .build();

        return ResponseEntity.ok(postPageResponseDto);
    }

    public ResponseEntity<PostDetailPageResponseDto> getPost(Long challengeId, Long postId, Pageable pageable) {
        Post post = postRepository.findPost(postId).orElseThrow(
                () -> new NotFoundException("인증 게시글이 존재하지 않습니다.")
        );

        Page<Comment> commentPage = commentRepository.findAllComment(pageable, postId);
        List<CommentResponseDto> commentDetailList = new ArrayList<>();

        for (Comment comment : commentPage) {
            CommentResponseDto commentDetailResponseDto = comment.toResponse();
            String commentCreatedAt = comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
            commentDetailResponseDto.setCreatedAt(commentCreatedAt);
            commentDetailList.add(commentDetailResponseDto);
        }
        PostDetailPageResponseDto postDetailPageResponseDto = post.toPageResponse(commentDetailList, commentPage.hasNext(), commentPage.getTotalElements());
        String postCreatedAt = post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
        postDetailPageResponseDto.setCreatedAt(postCreatedAt);
        return ResponseEntity.ok(postDetailPageResponseDto);
    }
}
