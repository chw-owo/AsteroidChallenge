package com.example.shortform.repository;

import com.example.shortform.domain.Challenge;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.*;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    List<Challenge> findAllByOrderByCreatedAtDesc();

    List<Challenge> findAllByTitleContaining(String search);

    @Query(value = "select c from Challenge c join fetch c.challengeImage join fetch c.category where c.category.id = :categoryId order by c.createdAt desc")
    List<Challenge> findAllByCategoryIdOrderByCreatedAtDesc(Long categoryId);

    List<Challenge> findAllByCategoryId(Long categoryId);

    @Query(value = "select distinct c from Challenge c inner join c.category left join c.tagChallenges t left join c.challengeImage i where c.title like %:keyword% or c.category.name like %:keyword% or t.tag.name like %:keyword%",
    countQuery = "select count(c) from Challenge c inner join c.category left join c.tagChallenges t where c.title like %:keyword% or c.category.name like %:keyword% or t.tag.name like %:keyword%")
    Page<Challenge> searchList(String keyword, Pageable pageable);

    @Query(value = "select distinct c from Challenge c " +
            "inner join c.category " +
            "left join c.challengeImage " +
            "left join c.tagChallenges " +
            "where c.category.id = :categoryId",
    countQuery = "select count(c) from Challenge c inner join c.category where c.category.id = :categoryId")
    Page<Challenge> findAllByCategoryId(Long categoryId, Pageable pageable);

    List<Challenge> findTop5ByCategoryIdOrderByCreatedAtDesc(Long id);

    @Query(value = "select distinct c from Challenge c inner join c.category left join c.challengeImage left join c.tagChallenges",
    countQuery = "select count(c) from Challenge c")
    Page<Challenge> findAllChallenge(Pageable pageable);

    @Query("select c from Challenge c where c.id = :challengeId")
    Optional<Challenge> findCheckChallenge(Long challengeId);

    @Query("select distinct c from Challenge c join fetch c.category join fetch c.user join fetch c.tagChallenges left join c.challengeImage where c.id = :challengeId")
    Optional<Challenge> findChallenge(Long challengeId);
}