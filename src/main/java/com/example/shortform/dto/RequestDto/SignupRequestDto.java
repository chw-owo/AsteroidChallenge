package com.example.shortform.dto.RequestDto;

import com.example.shortform.domain.User;
import lombok.*;

import java.io.Serializable;

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
                .point(point)
                .build();
    }
}
