package com.example.shortform.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SigninRequestDto {
    private String email;
    private String password;
}
