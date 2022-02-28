package com.example.shortform.dto.RequestDto;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProfileRequestDto {
    private String profileImage;
    private String password;
    private String passwordCheck;
}
