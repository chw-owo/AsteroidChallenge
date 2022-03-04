package com.example.shortform.dto.request;

import com.example.shortform.domain.User;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SignupRequestDto {

    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 6, message = "닉네임은 2~6글자 사이로 입력해주세요.")
    private String nickname;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,20}$",
                message = "비밀번호는 문자, 특수문자, 숫자를 포함한 8~20자 입니다.")
    private String password;

    @NotBlank(message = "비밀번호를 한번 더 입력해주세요.")
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
