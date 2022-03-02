package com.example.shortform.domain;

import lombok.*;

import javax.persistence.*;
import java.util.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class TagChallenge extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO) private Long id;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;


    @Builder
    public TagChallenge(Challenge challenge, Tag tag) {
        this.challenge = challenge;
        this.tag = tag;
        tag.getTagChallenges().add(this);
        challenge.getTagChallenges().add(this);

    }


}
