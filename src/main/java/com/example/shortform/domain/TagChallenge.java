package com.example.shortform.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class TagChallenge extends Timestamped{
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO) private Long id;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;
}
