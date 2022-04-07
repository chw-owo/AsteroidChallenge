package com.example.shortform.repository;

import com.example.shortform.domain.Post;
import com.example.shortform.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(value = "select p from Post p" +
            " inner join p.user" +
            " inner join p.challenge" +
            " inner join p.imageFile" +
            " where p.challenge.id = :challengeId",
            countQuery = "select count(p) from Post p " +
            "where p.challenge.id = :challengeId")
    Page<Post> findAllByChallengeId(Long challengeId, Pageable pageable);

    boolean existsByUserAndChallengeIdAndCreatedAtBetween(User user, Long challengeId, LocalDateTime localDateTime, LocalDateTime plusDays);

    @Query("select p from Post p " +
            "join fetch p.challenge " +
            "join fetch p.user" +
            " join fetch p.imageFile" +
            " where p.id = :postId")
    Optional<Post> findPost(Long postId);
}