package com.example.shortform.service;

import com.example.shortform.domain.Level;
import com.example.shortform.domain.Role;
import com.example.shortform.domain.User;
import com.example.shortform.dto.request.SignupRequestDto;
import com.example.shortform.dto.resonse.CMResponseDto;
import com.example.shortform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;

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
        sendSignupConfirmEmail(email, savedUser);

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
    public ResponseEntity<CMResponseDto> nicknameCheck(SignupRequestDto signupRequestDto) {

        if (!userRepository.findByNickname(signupRequestDto.getNickname()).isPresent())
            throw new IllegalArgumentException("이미 존재하는 닉네임 입니다.");

        return ResponseEntity.ok(new CMResponseDto("true"));
    }

    private void sendSignupConfirmEmail(String email, User savedUser) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(savedUser.getEmail());
        mailMessage.setSubject("소행성, 회원 가입 인증 메일");
        mailMessage.setText("/auth/check-email-token?token=" + savedUser.getEmailCheckToken() +
                "&email=" + email);
        javaMailSender.send(mailMessage);
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



}