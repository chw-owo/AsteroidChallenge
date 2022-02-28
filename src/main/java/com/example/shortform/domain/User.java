package com.example.shortform.domain;

import com.example.shortform.dto.ResponseDto.MemberResponseDto;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "point", nullable = false)
    private int point;

    @ManyToOne(optional = false)
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<UserChallenge> userChallenges = new ArrayList<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Challenge> challenges = new ArrayList<>();

    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
    }

    public MemberResponseDto toMemberResponse() {
        return MemberResponseDto.builder()
                .userId(id)
                .nickname(nickname)
                .profileImage(profileImage)
                .build();
    }
}
