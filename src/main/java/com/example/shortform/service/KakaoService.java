package com.example.shortform.service;

import com.example.shortform.config.auth.kakao.KakaoProfile;
import com.example.shortform.config.auth.kakao.OAuthToken;
import com.example.shortform.config.jwt.JwtAuthenticationProvider;
import com.example.shortform.config.jwt.TokenDto;
import com.example.shortform.domain.Role;
import com.example.shortform.domain.User;
import com.example.shortform.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoService {

    @Value("${KAKAO.CLIENT_ID}")
    private String CLIENT_ID;

    @Value("${KAKAO.REDIRECT_URL}")
    private String REDIRECT_URL;

    @Value("${KAKAO.RAW_PASSWORD}")
    private String RAW_PASSWORD;

    private String TOKEN_PREFIX = "Bearer ";

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Transactional
    public ResponseEntity<TokenDto> kakaoLogin(String code) {

        // POST 방식으로 key=value 데이터를 요청 (카카오쪽으로)
        // RestTemplate

        // HttpHeader 오브젝트 생성
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("grant_type", "authorization_code");
        params.add("client_id", CLIENT_ID);
        params.add("redirect_uri", REDIRECT_URL);
        params.add("code", code);

        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        // Http 요청하기 - Post, response변수의 응답 받음
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // 데이터를 오브젝트에 담는다.
        // Gson, Json Simple, ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        OAuthToken oAuthToken = null;

        try {
            oAuthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        RestTemplate restTemplate2 = new RestTemplate();

        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", TOKEN_PREFIX + oAuthToken.getAccess_token());
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");


        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
                new HttpEntity<>(headers2);

        // Http 요청하기 - Post, response변수의 응답 받음
        ResponseEntity<String> response2 = restTemplate2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        ObjectMapper objectMapper2 = new ObjectMapper();
        objectMapper2.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        KakaoProfile kakaoProfile = null;

        try {
            kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        log.info("kakao email: {}", kakaoProfile.getKakaoAccount().getEmail());
        log.info("kakao nickname: {}", kakaoProfile.getKakaoAccount().getProfile().getNickname());

        User userEntity = userRepository.findByProviderId(String.valueOf(kakaoProfile.getId()));

        // 가입되어있는지 확인
        if (userEntity == null) {

            String nickname = kakaoProfile.getKakaoAccount().getEmail().split("@")[0];

            // 강제 로그인 진행
            userEntity = User.builder()
                    .email(kakaoProfile.getKakaoAccount().getEmail())
                    .password(passwordEncoder.encode(RAW_PASSWORD)) // TODO 패스워드 properties에 입력
                    .nickname(nickname)
                    .rankingPoint(0)
                    .emailVerified(true)
                    .role(Role.ROLE_USER)
                    .provider("kakao")
                    .providerId(String.valueOf(kakaoProfile.getId()))
                    .build();

            userRepository.save(userEntity);
        }

        // 토큰 정보 생성
        TokenDto token = jwtAuthenticationProvider.createToken(userEntity);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtAuthenticationProvider.AUTHORIZATION_HEADER, TOKEN_PREFIX + token);

        return new ResponseEntity<>(token, httpHeaders, HttpStatus.OK);
    }
}
