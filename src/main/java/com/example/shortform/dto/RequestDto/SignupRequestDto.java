package com.example.shortform.dto.RequestDto;

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
}
