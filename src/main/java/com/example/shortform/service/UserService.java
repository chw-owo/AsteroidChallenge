package com.example.shortform.service;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.config.jwt.JwtAuthenticationProvider;
import com.example.shortform.config.jwt.TokenDto;
import com.example.shortform.domain.*;
import com.example.shortform.dto.request.*;
import com.example.shortform.dto.resonse.CMResponseDto;
import com.example.shortform.dto.resonse.UserInfo;
import com.example.shortform.dto.resonse.UserProfileInfo;
import com.example.shortform.exception.DuplicateException;
import com.example.shortform.exception.InvalidException;
import com.example.shortform.exception.NotFoundException;
import com.example.shortform.exception.UnauthorizedException;
import com.example.shortform.handler.MemberEventHandler;
import com.example.shortform.mail.EmailMessage;
import com.example.shortform.mail.EmailService;
import com.example.shortform.repository.LevelRepository;
import com.example.shortform.repository.NoticeRepository;
import com.example.shortform.repository.UserChallengeRepository;
import com.example.shortform.repository.UserRepository;
import com.example.shortform.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserFactory userFactory;
    private final UserRepository userRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final ChallengeService challengeService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final S3Uploader s3Uploader;
    private final HttpServletRequest request;
    private final MemberEventHandler memberEventHandler;
    private final TemplateEngine templateEngine;

    @Transactional
    public void signup(SignupRequestDto signupRequestDto) {

        // 유저 생성
        User user = userFactory.createUser(signupRequestDto);

        // 저장
        User savedUser = userRepository.save(user);

        // 메일 보내기
        savedUser.generateEmailCheckToken();
        sendSignupConfirmEmail(savedUser);

        // 회원가입 후 랭킹, 알람 저장
        memberEventHandler.memberSignUpEventListener(savedUser);
    }

    // 이메일 토큰 처리
    @Transactional
    public ResponseEntity<CMResponseDto> checkEmailToken(String token, String email) {
        // 이메일이 정확하지 않은 경우에 대한 에러처리
        User findUser = userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException("존재하지 않는 이메일입니다.")
        );

        // 토큰이 정확하지 않은 경우에 대한 에러처리
        if (!findUser.isValidToken(token))
            throw new UnauthorizedException("유효하지 않는 토큰입니다.");

        // 인증이 완료된 유저는 true로 변경
        findUser.setEmailVerified(true);

        return ResponseEntity.ok(new CMResponseDto("true"));
    }

    // 이메일 중복 체크
    public ResponseEntity<CMResponseDto> emailCheck(SignupRequestDto signupRequestDto) {

        if (!isExistEmail(signupRequestDto.getEmail()))
            throw new DuplicateException("이미 존재하는 이메일입니다.");

        return ResponseEntity.ok(new CMResponseDto("true"));
    }

    // 닉네임 중복 체크
    @Transactional(readOnly = true)
    public ResponseEntity<CMResponseDto> nicknameCheck(NickNameRequestDto nickNameRequestDto) {

        if (userRepository.findByNickname(nickNameRequestDto.getNickname()).isPresent())
            throw new DuplicateException("이미 존재하는 닉네임입니다.");

        return ResponseEntity.ok(new CMResponseDto("true"));
    }

    @Transactional
    public ResponseEntity<CMResponseDto> resendCheckEmailToken(EmailRequestDto emailRequestDto) {
        User findUser = userRepository.findByEmail(emailRequestDto.getEmail()).orElseThrow(
                () -> new NotFoundException("존재하지 않는 이메일입니다.")
        );

        if (!findUser.canSendConfirmEmail())
            throw new InvalidException("인증 이메일은 1시간에 한번만 전송할 수 있습니다.");

        // 이메일 인증 재전송
        sendSignupConfirmEmail(findUser);

        return ResponseEntity.ok(new CMResponseDto("true"));
    }

    @Transactional
    public ResponseEntity<TokenDto> login(SigninRequestDto signinRequestDto) {
        User userEntity = userRepository.findByEmail(signinRequestDto.getEmail()).orElseThrow(
                () -> new NotFoundException("존재하지 않는 이메일입니다.")
        );

        if (!passwordEncoder.matches(signinRequestDto.getPassword(), userEntity.getPassword()))
            throw new InvalidException("비밀번호가 일치하지 않습니다.");

        // 토큰 정보 생성
        TokenDto token = jwtAuthenticationProvider.createToken(userEntity);
        token.setEmail(signinRequestDto.getEmail());
        token.setEmailVerified(userEntity.isEmailVerified());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtAuthenticationProvider.AUTHORIZATION_HEADER, "Bearer "+ token);


        return new ResponseEntity<>(token, httpHeaders, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<CMResponseDto> sendTempPassword(EmailRequestDto emailRequestDto) {
        // 이메일이 유효한지 체크
        User findUser = userRepository.findByEmail(emailRequestDto.getEmail()).orElseThrow(
                () -> new NotFoundException("존재하지 않는 이메일입니다.")
        );

        // 인증 이메일 1시간 지났는지 체크
        if (!findUser.canSendConfirmEmail())
            throw new InvalidException("인증 이메일은 1시간에 한번만 전송할 수 있습니다.");

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
        String path = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        Context context = new Context();
        context.setVariable("link", path+"/auth/check-email-token?token=" + user.getEmailCheckToken() +
                "&email=" + user.getEmail());

        String message = templateEngine.process("mail/email-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(user.getEmail())
                .subject("소행성(소소한 행동 습관 형성 챌린지), 회원 가입 인증 메일")
                .message(message)
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

    // 로그인한 유저 정보 가져오기
    public UserInfo findUserInfo(User user) {

        List<UserChallenge> challengeInfo = userChallengeRepository.findAllByUser(user);

        int dailyCount = 0;
        for (UserChallenge userChallenge : challengeInfo) {
            String status = null;
            try {
                 status = challengeService.challengeStatus(userChallenge.getChallenge());
            } catch (ParseException e) {
                throw new InvalidException("잘못된 날짜 형식 입니다.");
            }

            if (!userChallenge.isDailyAuthenticated() && "진행중".equals(status))
                dailyCount++;
        }

        return UserInfo.of(user, dailyCount);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<CMResponseDto> passwordCheck(User user, UserPasswordRequestDto requestDto) {
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword()))
            throw new InvalidException("비밀번호가 일치하지 않습니다.");
        return ResponseEntity.ok(new CMResponseDto("true"));
    }

    @Transactional
    public ResponseEntity<CMResponseDto> updateProfile(Long userId, ProfileRequestDto requestDto, MultipartFile multipartFile,
                                                       PrincipalDetails principalDetails) throws IOException {

        if (!principalDetails.getUser().getId().equals(userId))
            throw new InvalidException("다른 사용자의 정보는 수정할 수 없습니다.");

        // 해당하는 유저 entity 찾기
        User findUser = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("존재하지 않는 유저입니다.")
        );
        // 이미지 S3 업로드
        String imgUrl;

        // 이미지 변경해주기
        if (multipartFile != null) {
            imgUrl = s3Uploader.upload(multipartFile, UUID.randomUUID() + multipartFile.getOriginalFilename());
            findUser.setProfileImage(imgUrl);
        }

        // 닉네임 변경
        if (!"".equals(requestDto.getNickname().trim())) {

            // 기존 닉네임과 중복일 경우
            if (findUser.getNickname().equals(requestDto.getNickname()))
                throw new DuplicateException("기존 닉네임과 동일합니다.");

            // 이미 있는 닉네임일 경우
            if (userRepository.findByNickname(requestDto.getNickname()).isPresent())
                throw new DuplicateException("이미 존재하는 닉네임입니다.");

            // 닉네임 변경
            findUser.setNickname(requestDto.getNickname());
        }

        // 비밀번호 변경
        if (!"".equals(requestDto.getPassword().trim()) ||
                !"".equals(requestDto.getPasswordCheck().trim())) {

            if(!isDuplicatePassword(requestDto.getPassword(), requestDto.getPasswordCheck()))
                throw new InvalidException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");

            if (passwordEncoder.matches(requestDto.getPassword(), findUser.getPassword()))
                throw new DuplicateException("기존 비밀번호와 동일합니다.");

            String encPassword = passwordEncoder.encode(requestDto.getPassword());

            findUser.setPassword(encPassword);
        }

        return ResponseEntity.ok(new CMResponseDto("true"));
    }

    @Transactional(readOnly = true)
    public UserProfileInfo getUserProfile(Long userId) {

        User findUser = userRepository.findUserInfo(userId).orElseThrow(
                () -> new NotFoundException("존재하지 않는 유저입니다.")
        );

        return UserProfileInfo.of(findUser);
    }
}
