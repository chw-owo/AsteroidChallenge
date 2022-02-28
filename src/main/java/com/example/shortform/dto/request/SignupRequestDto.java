package com.example.shortform.dto.request;

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
}
