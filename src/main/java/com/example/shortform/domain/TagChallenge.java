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
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;


    @Builder
    public TagChallenge(Challenge challenge, Tag tag) {
        this.challenge = challenge;
        this.tag = tag;
        tag.getTagChallenges().add(this);
        challenge.getTagChallenges().add(this);

    }


}
