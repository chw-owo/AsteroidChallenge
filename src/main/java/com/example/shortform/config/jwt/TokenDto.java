package com.example.shortform.config.jwt;


import lombok.*;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenDto {

    private String token;
    private String refreshToken;
    private String email;
    private boolean emailVerified;

    @Builder
    public TokenDto(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
