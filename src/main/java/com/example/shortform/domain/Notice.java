package com.example.shortform.domain;

import com.example.shortform.dto.ResponseDto.NoticeResponseDto;
import com.example.shortform.dto.resonse.MemberResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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
        FIRST
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
    private Long noticeLevel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public void setNoticeLevel(Long noticeLevel) {
        this.noticeLevel = noticeLevel;
    }

    public void setIs_read(boolean fact) {
        this.is_read = fact;
    }

    public NoticeResponseDto toResponse(MemberResponseDto memberResponseDto,
                                        String createdAt) {
        return NoticeResponseDto.builder()
                .is_read(is_read)
                .userInfo(memberResponseDto)
                .date(createdAt)
                .status(noticeType)
                .levelPoint(increasePoint)
                .challengeId(isIdNull())
                .title(isTitleNull())
                .postId(isIdNull())
                .build();
    }

    public Long isIdNull() {
        if (challenge == null)
            return null;
        else
            return challenge.getId();
    }

    public String isTitleNull() {
        if (challenge == null)
            return null;
        else
            return challenge.getTitle();
    }

}
