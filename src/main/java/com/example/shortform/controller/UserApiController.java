package com.example.shortform.controller;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.config.jwt.TokenDto;
import com.example.shortform.dto.request.*;
import com.example.shortform.dto.resonse.CMResponseDto;

import com.example.shortform.dto.resonse.UserChallengeInfo;
import com.example.shortform.dto.resonse.UserInfo;
import com.example.shortform.dto.resonse.UserProfileInfo;
import com.example.shortform.exception.InvalidException;
import com.example.shortform.exception.NotFoundException;
import com.example.shortform.service.ChallengeService;
import com.example.shortform.service.KakaoService;
import com.example.shortform.service.RankingService;
import com.example.shortform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;
    private final RankingService rankingService;
    private final KakaoService kakaoService;
    private final ChallengeService challengeService;

    @PostMapping("/auth/signup")
    public ResponseEntity<CMResponseDto> signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {
        userService.signup(signupRequestDto);
        return ResponseEntity.ok(new CMResponseDto("true"));
    }

    @PostMapping("/auth/email-check")
    public ResponseEntity<CMResponseDto> emailCheck(@RequestBody @Valid SignupRequestDto signupRequestDto) {
        return userService.emailCheck(signupRequestDto);
    }

    @PostMapping("/auth/nickname-check")
    public ResponseEntity<CMResponseDto> nicknameCheck(@RequestBody @Valid NickNameRequestDto nickNameRequestDto) {
        return userService.nicknameCheck(nickNameRequestDto);
    }

    @GetMapping("/auth/check-email-token")
    public void checkEmailToken(String token, String email, HttpServletResponse response) {
        userService.checkEmailToken(token, email);
        try {
            response.sendRedirect("https://www.sohangsung.co.kr/signup/complete");

        } catch (IOException e) {
            throw new InvalidException("유효하지 않은 주소입니다.");
        }

    }

    @PostMapping("/auth/resend-check-email")
    public ResponseEntity<CMResponseDto> resendCheckEmailToken(@RequestBody @Valid EmailRequestDto emailRequestDto) {
        return userService.resendCheckEmailToken(emailRequestDto);
    }

    @PostMapping("/auth/signin")
    public ResponseEntity<TokenDto> signin(@RequestBody @Valid SigninRequestDto signinRequestDto) {
        return userService.login(signinRequestDto);
    }

    @PostMapping("/auth/send-temp-password")
    public ResponseEntity<CMResponseDto> sendTempPassword(@RequestBody @Valid EmailRequestDto emailRequestDto) {
        return userService.sendTempPassword(emailRequestDto);
    }

    @GetMapping("/auth/kakao/callback")
    public ResponseEntity<TokenDto> kakaoCallback(String code) {
        return kakaoService.kakaoLogin(code);
    }

    @GetMapping("/auth/user-info")
    public ResponseEntity<UserInfo> findUserInfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails == null)
            throw new NotFoundException("존재하지 않는 유저입니다.");
        return ResponseEntity.ok(userService.findUserInfo(principalDetails.getUser()));
    }

    @PostMapping("/users/password-check")
    public ResponseEntity<CMResponseDto> passwordCheck(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                       @RequestBody @Valid UserPasswordRequestDto requestDto) {

        return userService.passwordCheck(principalDetails.getUser(), requestDto);
    }

    @PatchMapping("/users/{userId}")
    public ResponseEntity<CMResponseDto> updateProfile(@PathVariable Long userId,
                                                       @RequestPart("profile") ProfileRequestDto requestDto,
                                                       @RequestPart(value = "profileImage", required = false) MultipartFile multipartFile,
                                                       @AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {
        if (principalDetails == null)
            throw new NotFoundException("존재하지 않는 유저입니다.");

        return userService.updateProfile(userId, requestDto, multipartFile, principalDetails);
    }

    @GetMapping("/mypage/users/{userId}")
    public ResponseEntity<UserProfileInfo> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @GetMapping("/mypage/challenge/{userId}")
    public ResponseEntity<List<UserChallengeInfo>> getUserChallenge(@PathVariable Long userId) throws ParseException {
        return challengeService.getUserChallenge(userId);
    }
}

