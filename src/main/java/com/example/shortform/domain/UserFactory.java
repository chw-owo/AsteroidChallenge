package com.example.shortform.domain;

import com.example.shortform.dto.request.SignupRequestDto;
import com.example.shortform.exception.DuplicateException;
import com.example.shortform.exception.InvalidException;
import com.example.shortform.exception.NotFoundException;
import com.example.shortform.repository.LevelRepository;
import com.example.shortform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserFactory {

    private final LevelRepository levelRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public User createUser(SignupRequestDto signupRequestDto) {

        // 유효성 검사
        String email = signupRequestDto.getEmail();
        String rawPassword = signupRequestDto.getPassword();
        String passwordCheck = signupRequestDto.getPasswordCheck();

        if (!isExistEmail(signupRequestDto.getEmail()))
            throw new DuplicateException("이미 존재하는 이메일입니다.");

        if (!isPasswordMatched(email, rawPassword))
            throw new InvalidException("비밀번호에 아이디가 들어갈 수 없습니다.");

        if(!isDuplicatePassword(rawPassword, passwordCheck))
            throw new InvalidException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");

        Level level = levelRepository.findById(1L).get();

        // 비밀번호 암호화
        String encPassword = passwordEncoder.encode(rawPassword);

        // User 객체 생성
        return User.builder()
                .email(email)
                .nickname(signupRequestDto.getNickname())
                .password(encPassword)
                .level(level) // 기본 1레벨

                .rankingPoint(50) // 기본 포인트 50
                .yesterdayRankingPoint(50) // 이전 포인트도 50으로 설정


                .role(Role.ROLE_USER)
                .emailVerified(false)
                .newbie(true)
                .build();
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
