package com.example.shortform.repository;

import com.example.shortform.domain.Challenge;

import com.example.shortform.domain.TagChallenge;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface TagChallengeRepository extends JpaRepository<TagChallenge, Long> {
    List<TagChallenge> findAllByChallenge(Challenge challenge);

    boolean existsByChallengeAndTagName(Challenge challenge, String tagString);
}