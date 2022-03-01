package com.example.shortform.repository;

import com.example.shortform.domain.Tag;
import com.example.shortform.domain.TagChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagChallengeRepository extends JpaRepository<TagChallenge, Long> {
}