package com.example.shortform.repository;

import com.example.shortform.domain.AuthChallenge;
import com.example.shortform.domain.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface AuthChallengeRepository extends JpaRepository<AuthChallenge, Long> {


    AuthChallenge findByChallengeAndDate(Challenge challenge, LocalDate date);
}