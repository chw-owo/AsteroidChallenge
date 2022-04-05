package com.example.shortform.domain;

import com.example.shortform.exception.InvalidException;
import lombok.*;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "challenge_id", nullable = false)
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
        this.challengeDate = calcChallengeDate(challenge);
        this.dailyAuthenticated = false;
        this.authCount = 0;
    }

    private int calcChallengeDate(Challenge challenge) {
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

    public boolean getParticipateDate(int challengeDate, Challenge challenge) {
        // 챌린지 시작 후 참가가능 일수
        int possibleParticipateDate = (int) Math.ceil(challengeDate * 0.2); // 10일이면 2일

        // 참여가능 날짜
        String date1 = challenge.getStartDate().split(" ")[0];

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
            Date date = format.parse(date1);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, possibleParticipateDate);

            // 80%까지 참여할 수 있는 날짜
            String participateDate = format.format(cal.getTime());

            // 현재날짜 구하기
            LocalDate now = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            String nowDate = now.format(formatter);

            // 참여가능날짜 지났는지 비교
            Date before = format.parse(participateDate); // 2.27
            Date after = format.parse(nowDate); // 3.11

            return after.before(before);

        } catch (ParseException e) {
            throw new InvalidException("날짜 형식이 잘못되었습니다.");
        }

    }

    public boolean isSuccessChallenge() {
        // 성공일수(챌린지 진행일 * 0.8) > 인증횟수
        return (int)Math.ceil(this.getChallengeDate() * 0.8) < this.getAuthCount();
    }
}
