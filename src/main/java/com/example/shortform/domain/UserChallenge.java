package com.example.shortform.domain;

import com.example.shortform.exception.InvalidException;
import lombok.*;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class UserChallenge extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    // 챌린지 일수
    private int challengeDate;

    // 당일 인증 여부(성공하면 내가 해냄)
    private boolean dailyAuthenticated;

    // 인증 갯수
    private int authCount;

    public UserChallenge(Challenge challenge, User user) {
        this.challenge = challenge;
        this.user = user;
        this.challengeDate = getChallengeDate(challenge);
        this.dailyAuthenticated = false;
        this.authCount = 0;
    }

    private int getChallengeDate(Challenge challenge) {
        String date1 = challenge.getEndDate().split(" ")[0];
        String date2 = challenge.getStartDate().split(" ")[0];

        try {
            Date format1 = new SimpleDateFormat("yyyy.MM.dd").parse(date1);
            Date format2 = new SimpleDateFormat("yyyy.MM.dd").parse(date2);

            long diffSec = (format1.getTime() - format2.getTime()) / 1000;
            long diffDays = diffSec / (24 * 60 * 60);

            return (int) diffDays + 1;
        } catch (ParseException e) {
            throw new InvalidException("날짜 형식이 잘못되었습니다.");
        }
    }
}
