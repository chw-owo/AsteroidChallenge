package com.example.shortform.repository;

import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.User;
import com.example.shortform.domain.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {
    List<UserChallenge> findAllByChallenge(Challenge challenge);
}