package com.example.shortform.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProfileRequestDto {

    private String nickname;
    private String password;
    private String passwordCheck;
}
