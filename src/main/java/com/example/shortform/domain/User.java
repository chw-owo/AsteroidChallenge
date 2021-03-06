package com.example.shortform.domain;

import com.example.shortform.dto.resonse.ChatRoomMemberDto;
import com.example.shortform.dto.resonse.MemberResponseDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class User extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "nickname", unique = true, nullable = false)
    private String nickname;

    @Column(name = "profile_image", columnDefinition = "TEXT")
    private String profileImage;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "level_id")
    private Level level;

    @Column(name = "yesterday_rank", nullable = false)
    private int yesterdayRank;

    @Column(name = "rank_status")
    private String rankStatus;


    @Column(name = "point", nullable = false)
    private int rankingPoint;

    @Column(name = "yesterday_point")
    private int yesterdayRankingPoint;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "newbie", nullable = false)
    private boolean newbie;

    @Setter
    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime emailCheckTokenGeneratedAt;

    // kakao
    private String provider;

    private String providerId;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<UserChallenge> userChallenges = new ArrayList<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Challenge> challenges = new ArrayList<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<UserChatRoom> userChatRooms = new ArrayList<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Notice> notices = new ArrayList<>();

    public void setNewbie(boolean newbie) {
        this.newbie = newbie;
    }

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
    }

    public void changeTempPassword(String tempPassword) {
        this.password = tempPassword;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setPassword(String encPassword) {
        this.password = encPassword;
    }

    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    public void setRankingPoint(int point) {
        this.rankingPoint = Math.max(point, 0);
    }


    public MemberResponseDto toMemberResponse() {
        return MemberResponseDto.builder()
                .userId(id)
                .nickname(nickname)
                .profileImage(profileImage)
                .levelName(level.getName())
                .build();
    }

    public ChatRoomMemberDto toChatMemberResponse() {
        return ChatRoomMemberDto.builder()
                .profileUrl(profileImage)
                .email(email)
                .userId(id)
                .nickname(nickname)
                .levelName(level.getName())
                .build();
    }
    public void setRanking(String status, int todayRank, int todayRankingPoint){
        this.rankStatus = status;
        this.yesterdayRank = todayRank;
        this.yesterdayRankingPoint=todayRankingPoint;

    }

    public Object getRole() {
        return this.role;
    }

    @PrePersist
    public void prePersist(){
        this.yesterdayRank = -1;
        this.rankStatus = "new";
    }


    public void changeLevel(Level level) {
        this.level = level;
    }
}
