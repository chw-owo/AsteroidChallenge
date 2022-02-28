package com.example.shortform.domain;

import com.example.shortform.dto.RequestDto.ChallengeModifyRequestDto;
import com.example.shortform.dto.ResponseDto.ChallengeModifyResponseDto;
import com.example.shortform.dto.ResponseDto.ChallengeResponseDto;
import com.example.shortform.dto.ResponseDto.MemberResponseDto;
import com.example.shortform.dto.ResponseDto.TagNameResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Challenge extends Timestamped{
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "challenge_image")
    private String challengeImage;

    @Column(name = "max_member", nullable = false)
    private int maxMember;

    @Column(name = "current_member", nullable = false)
    private int currentMember;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "is_private", nullable = false)
    private Boolean isPrivate = false;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ChallengeStatus status;

    @OneToMany(mappedBy = "challenge", orphanRemoval = true)
    private List<UserChallenge> userChallenges = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.MERGE, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "challenge", orphanRemoval = true)
    private List<TagChallenge> tagChallenges = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public ChallengeResponseDto toResponse(List<TagNameResponseDto> tagNameList, List<MemberResponseDto> memberList) {
        return ChallengeResponseDto.builder()
                .challengeId(id)
                .userId(user.getId())
                .title(title)
                .content(content)
                .categoryName(category.getName())
                .challengeImage(challengeImage)
                .maxMember(maxMember)
                .currentMember(currentMember)
                .startDate(startDate)
                .endDate(endDate)
                .isPrivate(isPrivate)
                .tagNameList(tagNameList)
                .members(memberList)
                .build();
    }

    public void update(ChallengeModifyRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.challengeImage = requestDto.getChallengeImage();
        this.category.setName(requestDto.getCategory());
    }

    public ChallengeModifyResponseDto toResponse() {
        return ChallengeModifyResponseDto.builder()
                .challengeId(this.id)
                .build();
    }
}
