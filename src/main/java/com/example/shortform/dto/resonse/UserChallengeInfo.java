package com.example.shortform.dto.resonse;

import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.Post;
import com.example.shortform.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserChallengeInfo {

    private Long userId;
    private Long challengeId;
    private String title;
    private String category;
    private List<String> challengeImage;
    private int maxMember;
    private int currentMember;
    private String startDate;
    private String endDate;
    private Boolean isPrivate;
    private List<String> tagName;
    private String status;
    private String dailyAuth;

    public static UserChallengeInfo of(Challenge challenge, String status,
                                       List<String> tagChallengeStrings, List<String> challengeImages, String dailyAuth) {
        return UserChallengeInfo.builder()
                .userId(challenge.getUser().getId())
                .challengeId(challenge.getId())
                .title(challenge.getTitle())
                .category(challenge.getCategory().getName())
                .challengeImage(challengeImages)
                .maxMember(challenge.getMaxMember())
                .currentMember(challenge.getCurrentMember())
                .startDate(challenge.getStartDate())
                .endDate(challenge.getEndDate())
                .isPrivate(challenge.getIsPrivate())
                .tagName(tagChallengeStrings)
                .status(status)
                .dailyAuth(dailyAuth)
                .build();
    };
}
