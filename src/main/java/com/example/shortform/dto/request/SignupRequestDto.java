package com.example.shortform.dto.request;

import com.example.shortform.domain.User;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SignupRequestDto {
    private String email;
    private String nickname;
    private String password;
    private String passwordCheck;

    public User toEntity(int point) {
        return User.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .rankingPoint(point)
                .build();
    }
}
