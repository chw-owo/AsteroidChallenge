package com.example.shortform.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

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
