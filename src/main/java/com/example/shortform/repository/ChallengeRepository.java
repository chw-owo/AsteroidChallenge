package com.example.shortform.repository;

import com.example.shortform.domain.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.*;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    List<Challenge> findAllByOrderByCreatedAtDesc();

    List<Challenge> findAllByTitleContaining(String search);

    List<Challenge> findAllByCategoryIdOrderByCreatedAtDesc(Long categoryId);
}