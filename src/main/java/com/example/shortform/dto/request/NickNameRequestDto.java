package com.example.shortform.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class NickNameRequestDto {

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 8, message = "닉네임은 2~6글자 사이로 입력해주세요.")
    private String nickname;
}
