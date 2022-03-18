package com.example.shortform.repository;

import com.example.shortform.domain.Challenge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.*;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    List<Challenge> findAllByOrderByCreatedAtDesc();

    List<Challenge> findAllByTitleContaining(String search);

    List<Challenge> findAllByCategoryIdOrderByCreatedAtDesc(Long categoryId);

    @Query("select distinct c from Challenge c left join c.tagChallenges t where c.title like %:keyword% or c.category.name like %:keyword% or t.tag.name like %:keyword%")
    Page<Challenge> searchList(String keyword, Pageable pageable);

    Page<Challenge> findAllByCategoryId(Long categoryId, Pageable pageable);
}