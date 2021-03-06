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

        User user = userFactory.createUser(signupRequestDto);
        User savedUser = userRepository.save(user);

        savedUser.generateEmailCheckToken();
        sendSignupConfirmEmail(savedUser);

        memberEventHandler.memberSignUpEventListener(savedUser);
    }

    @Transactional
    public ResponseEntity<CMResponseDto> checkEmailToken(String token, String email) {

        User findUser = userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException("???????????? ?????? ??????????????????.")
        );

        if (!findUser.isValidToken(token))
            throw new UnauthorizedException("???????????? ?????? ???????????????.");

        findUser.setEmailVerified(true);

        return ResponseEntity.ok(new CMResponseDto("true"));
    }

    public ResponseEntity<CMResponseDto> emailCheck(SignupRequestDto signupRequestDto) {

        if (!isExistEmail(signupRequestDto.getEmail()))
            throw new DuplicateException("?????? ???????????? ??????????????????.");

        return ResponseEntity.ok(new CMResponseDto("true"));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<CMResponseDto> nicknameCheck(NickNameRequestDto nickNameRequestDto) {

        if (userRepository.findByNickname(nickNameRequestDto.getNickname()).isPresent())
            throw new DuplicateException("?????? ???????????? ??????????????????.");

        return ResponseEntity.ok(new CMResponseDto("true"));
    }

    @Transactional
    public ResponseEntity<CMResponseDto> resendCheckEmailToken(EmailRequestDto emailRequestDto) {

        User findUser = userRepository.findByEmail(emailRequestDto.getEmail()).orElseThrow(
                () -> new NotFoundException("???????????? ?????? ??????????????????.")
        );

        if (!findUser.canSendConfirmEmail())
            throw new InvalidException("?????? ???????????? 1????????? ????????? ????????? ??? ????????????.");

        sendSignupConfirmEmail(findUser);

        return ResponseEntity.ok(new CMResponseDto("true"));
    }

    @Transactional
    public ResponseEntity<TokenDto> login(SigninRequestDto signinRequestDto) {

        User userEntity = userRepository.findByEmail(signinRequestDto.getEmail()).orElseThrow(
                () -> new NotFoundException("???????????? ?????? ??????????????????.")
        );

        if (!passwordEncoder.matches(signinRequestDto.getPassword(), userEntity.getPassword()))
            throw new InvalidException("??????????????? ???????????? ????????????.");

        TokenDto token = jwtAuthenticationProvider.createToken(userEntity);
        token.setEmail(signinRequestDto.getEmail());
        token.setEmailVerified(userEntity.isEmailVerified());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtAuthenticationProvider.AUTHORIZATION_HEADER, "Bearer "+ token);

        return new ResponseEntity<>(token, httpHeaders, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<CMResponseDto> sendTempPassword(EmailRequestDto emailRequestDto) {

        User findUser = userRepository.findByEmail(emailRequestDto.getEmail()).orElseThrow(
                () -> new NotFoundException("???????????? ?????? ??????????????????.")
        );

        // ?????? ????????? 1?????? ???????????? ??????
        if (!findUser.canSendConfirmEmail())
            throw new InvalidException("?????? ???????????? 1????????? ????????? ????????? ??? ????????????.");

        String tempPassword = temporaryPassword(10); // 8?????? ???????????? ?????? ???????????? ??????

        String tempEncPassword = passwordEncoder.encode(tempPassword); // ?????????
        findUser.changeTempPassword(tempEncPassword);

        sendTempPasswordConfirmEmail(findUser, tempPassword);

        return ResponseEntity.ok(new CMResponseDto("true"));
    }

    public UserInfo findUserInfo(User user) {

        List<UserChallenge> challengeInfo = userChallengeRepository.findAllByUser(user);

        int dailyCount = 0;
        for (UserChallenge userChallenge : challengeInfo) {
            String status = null;
            try {
                 status = challengeService.challengeStatus(userChallenge.getChallenge());
            } catch (ParseException e) {
                throw new InvalidException("????????? ?????? ?????? ?????????.");
            }

            if (!userChallenge.isDailyAuthenticated() && "?????????".equals(status))
                dailyCount++;
        }

        return UserInfo.of(user, dailyCount);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<CMResponseDto> passwordCheck(User user, UserPasswordRequestDto requestDto) {
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword()))
            throw new InvalidException("??????????????? ???????????? ????????????.");
        return ResponseEntity.ok(new CMResponseDto("true"));
    }

    @Transactional
    public ResponseEntity<CMResponseDto> updateProfile(Long userId, ProfileRequestDto requestDto, MultipartFile multipartFile,
                                                       PrincipalDetails principalDetails) throws IOException {

        if (!principalDetails.getUser().getId().equals(userId))
            throw new InvalidException("?????? ???????????? ????????? ????????? ??? ????????????.");

        User findUser = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("???????????? ?????? ???????????????.")
        );
        String imgUrl;

        if (multipartFile != null) {
            imgUrl = s3Uploader.upload(multipartFile, UUID.randomUUID() + multipartFile.getOriginalFilename());
            findUser.setProfileImage(imgUrl);
        }

        if (!"".equals(requestDto.getNickname().trim())) {

            if (findUser.getNickname().equals(requestDto.getNickname()))
                throw new DuplicateException("?????? ???????????? ???????????????.");

            if (userRepository.findByNickname(requestDto.getNickname()).isPresent())
                throw new DuplicateException("?????? ???????????? ??????????????????.");

            findUser.setNickname(requestDto.getNickname());
        }

        if (!"".equals(requestDto.getPassword().trim()) ||
                !"".equals(requestDto.getPasswordCheck().trim())) {

            if(!isDuplicatePassword(requestDto.getPassword(), requestDto.getPasswordCheck()))
                throw new InvalidException("??????????????? ???????????? ????????? ???????????? ????????????.");

            if (passwordEncoder.matches(requestDto.getPassword(), findUser.getPassword()))
                throw new DuplicateException("?????? ??????????????? ???????????????.");

            String encPassword = passwordEncoder.encode(requestDto.getPassword());

            findUser.setPassword(encPassword);
        }

        return ResponseEntity.ok(new CMResponseDto("true"));
    }

    @Transactional(readOnly = true)
    public UserProfileInfo getUserProfile(Long userId) {

        User findUser = userRepository.findUserInfo(userId).orElseThrow(
                () -> new NotFoundException("???????????? ?????? ???????????????.")
        );

        return UserProfileInfo.of(findUser);
    }

    private void sendSignupConfirmEmail(User user) {
        String path = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        Context context = new Context();
        context.setVariable("link", path+"/auth/check-email-token?token=" + user.getEmailCheckToken() +
                "&email=" + user.getEmail());

        String message = templateEngine.process("mail/email-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(user.getEmail())
                .subject("?????????(????????? ?????? ?????? ?????? ?????????), ?????? ?????? ?????? ??????")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    private void sendTempPasswordConfirmEmail(User user, String tempPwd) {
        EmailMessage emailMessage = EmailMessage.builder()
                .to(user.getEmail())
                .subject("?????????(????????? ?????? ?????? ?????? ?????????), ?????? ???????????? ??????")
                .message("<p>?????? ????????????: <b>" + tempPwd + "</b></p><br>" +
                        "<p>????????? ??? ??????????????? ??????????????????.</p>")
                .build();

        emailService.sendEmail(emailMessage);
    }

    private boolean isDuplicatePassword(String rawPassword, String pwCheck) {
        return rawPassword.equals(pwCheck);
    }

    private boolean isExistEmail(String email) {
        return !userRepository.findByEmail(email).isPresent();
    }

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
