package com.example.shortform.domain;

import com.example.shortform.dto.ResponseDto.NoticeResponseDto;
import com.example.shortform.dto.resonse.MemberResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Notice extends Timestamped {

    public enum NoticeType {
        INITIAL,
        RECOMMEND,
        MORNING_CALL,
        WRITE,
        SUCCESS,
        LEVEL,
        SIGNIN,
        FIRST,
        RECORD
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "notice_type", nullable = false)
    private NoticeType noticeType;

    @Column(name = "is_read", nullable = false)
    private boolean is_read;

    @Column(name = "increase_point")
    private int increasePoint;

    @Column(name = "challenge_cnt")
    private int challengeCnt;

    @Column(name = "notice_level")
    private long noticeLevel;

    @Column(name = "is_success")
    private Boolean isSuccess;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "challenge_id")
    private Long challengeId;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "post_id")
    private Long postId;

    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public void setNoticeLevel(Long noticeLevel) {
        this.noticeLevel = noticeLevel;
    }

    public void setIs_read(boolean fact) {
        this.is_read = fact;
    }

    public void setNoticeType(NoticeType type) {
        this.noticeType = type;
    }

    public Notice(User user, List<Challenge> challengeList) {
        this.noticeType = NoticeType.MORNING_CALL;
        this.is_read = false;
        this.user = user;
        this.challengeCnt = challengeList.size();
        this.challengeId = challengeList.get(0).getId();
    }

    public Notice(User user, Challenge challenge) {
        this.noticeType = NoticeType.INITIAL;
        this.is_read = false;
        this.user = user;
        this.challengeId = challenge.getId();
    }

    public Notice(User user, Challenge challenge, int point) {
        this.noticeType = NoticeType.SUCCESS;
        this.is_read = false;
        this.user = user;
        this.isSuccess = true;
        this.challengeId = challenge.getId();
        this.increasePoint = point;
    }

    public Notice(User user){
        this.noticeType = NoticeType.RECOMMEND;
        this.is_read = false;
        this.user = user;
    }

    public Notice(User user, Challenge challenge, Post post, int point) {
        this.noticeType = NoticeType.WRITE;
        this.is_read = false;
        this.user = user;
        this.challengeId = challenge.getId();
        this.postId = post.getId();
        this.roomId = challenge.getChatRoom().getId();
        this.increasePoint = point;
    }

    public Notice(User user, int point) {
        this.noticeType = NoticeType.FIRST;
        this.is_read = false;
        this.user = user;
        this.increasePoint = point;
    }

    public Notice(User user, Level level) {
        this.noticeType = NoticeType.LEVEL;
        this.is_read = false;
        this.user = user;
        this.noticeLevel = level.getId();
    }

    public NoticeResponseDto toChallengeResponse(MemberResponseDto memberResponseDto,
                                                 String createdAt,
                                                 Challenge challenge) {
        return NoticeResponseDto.builder()
                .read(is_read)
                .userInfo(memberResponseDto)
                .date(createdAt)
                .status(noticeType)
                .challengeCnt(challengeCnt)
                .levelPoint(increasePoint)
                .challengeId(challengeId)
                .title(challenge.getTitle())
                .roomId(challenge.getChatRoom().getId())
                .build();
    }

    public NoticeResponseDto toResponse(MemberResponseDto memberResponseDto,
                                        String createdAt) {
        return NoticeResponseDto.builder()
                .read(is_read)
                .userInfo(memberResponseDto)
                .levelPoint(increasePoint)
                .challengeCnt(challengeCnt)
                .date(createdAt)
                .status(noticeType)
                .build();
    }

}
