package com.example.shortform.repository;

import com.example.shortform.domain.Category;
import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.*;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    List<Challenge> findAllByOrderByCreatedAt();


    List<Challenge> findAllByTitleContaining(String search);

    List<Challenge> findAllByCategoryId(Category categoryId);
}