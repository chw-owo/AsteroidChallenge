package com.example.shortform.dto.resonse;

import com.example.shortform.domain.User;
import lombok.*;

@Getter
@Builder
public class UserInfo {

    private Long userId;
    private String email;
    private String nickname;
    private String profileUrl;

    private int dailyCount;

    public static UserInfo of(User user, int dailyCount) {
        return UserInfo.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileUrl(user.getProfileImage())
                .dailyCount(dailyCount)
                .build();
    }

}
