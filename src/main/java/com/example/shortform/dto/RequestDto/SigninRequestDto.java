package com.example.shortform.dto.RequestDto;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SigninRequestDto {
    private String email;
    private String password;
}