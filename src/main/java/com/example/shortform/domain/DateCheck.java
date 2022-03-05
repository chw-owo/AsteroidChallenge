package com.example.shortform.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class DateCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

//    @ManyToOne
//    @JoinColumn(name = "user_challenge_id")
//    private UserChallenge userChallenge;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    public DateCheck(LocalDateTime createdAt) {
        this.date = createdAt;
    }


//    public void setUserChallenge(Challenge challenge, User user) {
//        this.userChallenge = new UserChallenge(challenge,user);
//    }
}
