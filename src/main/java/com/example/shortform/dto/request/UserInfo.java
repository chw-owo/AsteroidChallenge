package com.example.shortform.dto.request;

import com.example.shortform.domain.User;
import lombok.*;

@Getter
@Builder
public class UserInfo {

    private Long userId;
    private String email;
    private String nickname;
    private String profileUrl;

    public static UserInfo of(User user) {
        return UserInfo.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileUrl(user.getProfileImage())
                .build();
    }

}
