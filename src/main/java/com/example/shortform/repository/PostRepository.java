package com.example.shortform.repository;

import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.Post;
import com.example.shortform.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByChallengeId(Long challengeId);

    Post findTop1ByOrderByCreatedAtDesc();

    List<Post> findAllByUser(User user);
}