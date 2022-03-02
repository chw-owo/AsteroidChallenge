package com.example.shortform.service;

import com.example.shortform.config.auth.kakao.KakaoProfile;
import com.example.shortform.config.auth.kakao.OAuthToken;
import com.example.shortform.config.jwt.TokenDto;
import com.example.shortform.config.jwt.JwtAuthenticationProvider;
import com.example.shortform.domain.Role;
import com.example.shortform.domain.User;
import com.example.shortform.dto.request.EmailRequestDto;
import com.example.shortform.dto.request.SigninRequestDto;
import com.example.shortform.dto.request.SignupRequestDto;
import com.example.shortform.dto.resonse.CMResponseDto;
import com.example.shortform.mail.EmailMessage;
import com.example.shortform.mail.EmailService;
import com.example.shortform.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Random;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Transactional
    public ResponseEntity<CMResponseDto> signup(SignupRequestDto signupRequestDto) {

        // 유효성 검사
        String email = signupRequestDto.getEmail();
        String rawPassword = signupRequestDto.getPassword();
        String passwordCheck = signupRequestDto.getPasswordCheck();

        if (!isPasswordMatched(email, rawPassword))
            throw new IllegalArgumentException("비밀번호에 아이디가 들어갈 수 없습니다.");

        if(!isDuplicatePassword(rawPassword, passwordCheck))
            throw new IllegalArgumentException("비밀번호 확인이 일치하지 않습니다.");

        // 비밀번호 암호화
        String encPassword = passwordEncoder.encode(rawPassword);

        // User 객체 생성
        User user = User.builder()
                .email(email)
                .nickname(signupRequestDto.getNickname())
                .password(encPassword)
                //.level(null) // TODO level 값 넣어줘야 한다.
                .point(0) // 기본 포인트 0
                .role(Role.ROLE_USER)
                .emailVerified(false)
                .build();

        // 저장
        User savedUser = userRepository.save(user);

        // TODO 인증 메일 보내기
        savedUser.generateEmailCheckToken();
        sendSignupConfirmEmail(savedUser);

        return ResponseEntity.ok(new CMResponseDto("true"));
    }

    // 이메일 토큰 처리
    @Transactional
    public ResponseEntity<CMResponseDto> checkEmailToken(String token, String email) {
        // 이메일이 정확하지 않은 경우에 대한 에러처리
        User findUser = userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 이메일")
        );

        // 토큰이 정확하지 않은 경우에 대한 에러처리
        if (!findUser.isValidToken(token))
            throw new IllegalArgumentException("토큰이 정확하지 않습니다.");

        // 인증이 완료된 유저는 true로 변경
        findUser.setEmailVerified(true);

        return ResponseEntity.ok(new CMResponseDto("회원 인증 완료"));
    }

    // 이메일 중복 체크
    public ResponseEntity<CMResponseDto> emailCheck(SignupRequestDto signupRequestDto) {

        if (!isExistEmail(signupRequestDto.getEmail()))
            throw new IllegalArgumentException("이미 존재하는 아매알 입니다.");

        return ResponseEntity.ok(new CMResponseDto("true"));
    }

    // 닉네임 중복 체크
    @Transactional(readOnly = true)
    public ResponseEntity<CMResponseDto> nicknameCheck(SignupRequestDto signupRequestDto) {

        if (!userRepository.findByNickname(signupRequestDto.getNickname()).isPresent())
            throw new IllegalArgumentException("이미 존재하는 닉네임 입니다.");

        return ResponseEntity.ok(new CMResponseDto("true"));
    }

    @Transactional
    public ResponseEntity<CMResponseDto> resendCheckEmailToken(EmailRequestDto emailRequestDto) {
        User findUser = userRepository.findByEmail(emailRequestDto.getEmail()).orElseThrow(
                () -> new IllegalArgumentException("가입되지 않은 이메일입니다.")
        );

        if (!findUser.canSendConfirmEmail())
            throw new IllegalArgumentException("인증 이메일은 1시간에 한번만 전송할 수 있습니다.");

        // 이메일 인증 재전송
        sendSignupConfirmEmail(findUser);

        return ResponseEntity.ok(new CMResponseDto("true"));
    }

    @Transactional
    public ResponseEntity<TokenDto> login(SigninRequestDto signinRequestDto) {
        User userEntity = userRepository.findByEmail(signinRequestDto.getEmail()).orElseThrow(
                () -> new IllegalArgumentException("가입되지 않은 이메일입니다.")
        );

        if (!passwordEncoder.matches(signinRequestDto.getPassword(), userEntity.getPassword()))
            throw new IllegalArgumentException("패스워드가 일치하지 않습니다.");

        // 토큰 정보 생성
        TokenDto token = jwtAuthenticationProvider.createToken(userEntity);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtAuthenticationProvider.AUTHORIZATION_HEADER, "Bearer "+ token);


        return new ResponseEntity<>(token, httpHeaders, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<CMResponseDto> sendTempPassword(EmailRequestDto emailRequestDto) {
        // 이메일이 유효한지 체크
        User findUser = userRepository.findByEmail(emailRequestDto.getEmail()).orElseThrow(
                () -> new IllegalArgumentException("가입되지 않은 이메일 입니다.")
        );

        // 인증 이메일 1시간 지났는지 체크
        if (!findUser.canSendConfirmEmail())
            throw new IllegalArgumentException("인증 이메일은 1시간에 한번만 전송할 수 있습니다.");

        // 임시 비밀번호 발급
        String tempPassword = temporaryPassword(10); // 8글자 랜덤으로 임시 비밀번호 생성

        // 유저의 비밀번호를 임시 비밀번호로 변경
        String tempEncPassword = passwordEncoder.encode(tempPassword); // 암호화
        findUser.changeTempPassword(tempEncPassword);

        // 이메일 전송
        sendTempPasswordConfirmEmail(findUser, tempPassword);

        return ResponseEntity.ok(new CMResponseDto("true"));
    }

    private void sendSignupConfirmEmail(User user) {
        EmailMessage emailMessage = EmailMessage.builder()
                .to(user.getEmail())
                .subject("소행성(소소한 행동 습관 형성 챌린지), 회원 가입 인증 메일")
                .message("/auth/check-email-token?token=" + user.getEmailCheckToken() +
                        "&email=" + user.getEmail())
                .build();

        emailService.sendEmail(emailMessage);
    }

    private void sendTempPasswordConfirmEmail(User user, String tempPwd) {
        EmailMessage emailMessage = EmailMessage.builder()
                .to(user.getEmail())
                .subject("소행성(소소한 행동 습관 형성 챌린지), 임시 비밀번호 발급")
                .message("<p>임시 비밀번호: <b>" + tempPwd + "</b></p><br>" +
                        "<p>로그인 후 비밀번호를 변경해주세요.</p>")
                .build();

        emailService.sendEmail(emailMessage);
    }

    private boolean isDuplicatePassword(String rawPassword, String pwCheck) {
        return rawPassword.equals(pwCheck);
    }

    private boolean isExistEmail(String email) {
        return !userRepository.findByEmail(email).isPresent();
    }

    private boolean isPasswordMatched(String email, String rawPassword) {
        String domain = email.split("@")[0];
        return !rawPassword.contains(domain);
    }

    // 임시 비밀번호 생성 메서드
    private String temporaryPassword(int size) {
        StringBuffer buffer = new StringBuffer();
        Random random = new Random();
        String chars[] = ("A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z," +
                "a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,0,1,2,3,4,5,6,7,8,9").split(",");
        for (int i = 0; i < size; i++) {
            buffer.append(chars[random.nextInt(chars.length)]);
        }
        buffer.append("!a1");
        return buffer.toString();
    }
}
