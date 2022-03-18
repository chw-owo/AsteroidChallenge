package com.example.shortform.config.auth.kakao;

import lombok.Data;

@Data
public class KakaoProfile {

    public Integer id;
    public String connectedAt;
    public Properties properties;
    public KakaoAccount kakaoAccount;

    @Data
    public class Properties {

        public String nickname;

    }

    @Data
    public class KakaoAccount {

        public Boolean profileNicknameNeedsAgreement;
        public Profile profile;
        public Boolean hasEmail;
        public Boolean emailNeedsAgreement;
        public Boolean isEmailValid;
        public Boolean isEmailVerified;
        public String email;

        @Data
        public class Profile {

            public String nickname;

        }

    }

}







