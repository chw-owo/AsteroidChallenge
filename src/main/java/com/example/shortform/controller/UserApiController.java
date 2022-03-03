package com.example.shortform.controller;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.config.jwt.TokenDto;
import com.example.shortform.dto.request.*;
import com.example.shortform.dto.resonse.CMResponseDto;
import com.example.shortform.dto.resonse.UserProfileInfo;
import com.example.shortform.service.KakaoService;
import com.example.shortform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;
    private final KakaoService kakaoService;

    // 회원가입
    @PostMapping("/auth/signup")
    public ResponseEntity<CMResponseDto> signup(@RequestBody SignupRequestDto signupRequestDto) {
        return userService.signup(signupRequestDto);
    }

    // 이메일 중복체크
    @PostMapping("/auth/email-check")
    public ResponseEntity<CMResponseDto> emailCheck(@RequestBody SignupRequestDto signupRequestDto) {
        return userService.emailCheck(signupRequestDto);
    }

    // 닉네임 중복체크
    @PostMapping("/auth/nickname-check")
    public ResponseEntity<CMResponseDto> nicknameCheck(@RequestBody SignupRequestDto signupRequestDto) {
        return userService.nicknameCheck(signupRequestDto);
    }

    // 이메일 인증 확인
    @GetMapping("/auth/check-email-token")
    public ResponseEntity<CMResponseDto> checkEmailToken(String token, String email) {
        return userService.checkEmailToken(token, email);
    }

    // 이메일 인증 재전송
    @PostMapping("/auth/resend-check-email")
    public ResponseEntity<CMResponseDto> resendCheckEmailToken(@RequestBody EmailRequestDto emailRequestDto) {
        return userService.resendCheckEmailToken(emailRequestDto);
    }

    // 로그인
    @PostMapping("/auth/signin")
    public ResponseEntity<TokenDto> signin(@RequestBody SigninRequestDto signinRequestDto) {
        return userService.login(signinRequestDto);
    }

    // 임시 비밀번호 발급
    @PostMapping("/auth/send-temp-password")
    public ResponseEntity<CMResponseDto> sendTempPassword(@RequestBody EmailRequestDto emailRequestDto) {
        return userService.sendTempPassword(emailRequestDto);
    }

    // 카카오 로그인
    @GetMapping("/auth/kakao/callback")
    public ResponseEntity<TokenDto> kakaoCallback(String code) {
        return kakaoService.kakaoLogin(code);
    }

    // 로그인 유저 정보 확인
    @GetMapping("/auth/user-info")
    public ResponseEntity<UserInfo> findUserInfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails == null)
            throw new IllegalArgumentException("유저 정보가 없습니다.");
        return ResponseEntity.ok(userService.findUserInfo(principalDetails.getUser()));
    }

    // 비밀번호 확인
    @PostMapping("/users/password-check")
    public ResponseEntity<CMResponseDto> passwordCheck(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                       @RequestBody SigninRequestDto requestDto) {
        // PrincipalDetails는 상태값이 디테치드 상태, 영속화 되어있지는 않음
        // 아무리 변경하더라도 변경을 감지 하지 않음
        // save를 호출해서 넣어주자.
        return userService.passwordCheck(principalDetails.getUser(), requestDto);
    }

    // 회원 프로필 수정
    @PutMapping("/users/{userId}")
    public ResponseEntity<CMResponseDto> updateProfile(@PathVariable Long userId,
                                                       @RequestPart("profile") ProfileRequestDto requestDto,
                                                       @RequestPart(value = "profileImage", required = false) MultipartFile multipartFile) throws IOException {
        return userService.updateProfile(userId, requestDto, multipartFile);
    }

    // 유저 정보 조회
    @GetMapping("/mypage/users/{userId}")
    public ResponseEntity<UserProfileInfo> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

}

