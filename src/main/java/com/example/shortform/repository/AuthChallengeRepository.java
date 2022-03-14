package com.example.shortform.repository;

import com.example.shortform.domain.AuthChallenge;
import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.User;
import com.example.shortform.domain.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AuthChallengeRepository extends JpaRepository<AuthChallenge, Long> {


    AuthChallenge findByChallengeAndDate(Challenge challenge, LocalDate date);
}