package com.example.shortform.dto.ResponseDto;

import com.example.shortform.domain.User;

public class RankingResponseDto {

    private Long userId;
    private String nickname;
    private String profileImage;
    private int point;
    private String level;

    public RankingResponseDto(User user){
        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
        this.point = user.getPoint();
        this.level = user.getLevel().getName();
    }
}
