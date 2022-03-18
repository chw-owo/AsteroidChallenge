package com.example.shortform.repository;

import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.Post;
import com.example.shortform.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByChallengeIdOrderByCreatedAtDesc(Long challengeId);

    Post findTop1ByOrderByCreatedAtDesc();

    List<Post> findAllByUser(User user);

    Page<Post> findAllByChallengeId(Long challengeId, Pageable pageable);
}