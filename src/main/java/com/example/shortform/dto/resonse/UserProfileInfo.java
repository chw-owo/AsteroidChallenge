package com.example.shortform.dto.resonse;

import com.example.shortform.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileInfo {

    private Long userId;
    private String email;
    private String nickname;
    private String profileUrl;
    private int rankingPoint;


    private String levelName;
    private String levelIcon;
    private int experiencePoint;

    private boolean isKakao;

    public static UserProfileInfo of(User user) {
        return UserProfileInfo.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .rankingPoint(user.getRankingPoint())
                .nickname(user.getNickname())
                .profileUrl(user.getProfileImage())

                .levelName(user.getLevel().getName())
                .levelIcon(user.getLevel().getLevelIcon())
                .experiencePoint(user.getLevel().getNextPoint())

                .isKakao(user.getProvider() != null)
                .build();
    }
}
