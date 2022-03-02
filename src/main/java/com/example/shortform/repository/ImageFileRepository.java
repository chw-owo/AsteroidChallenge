package com.example.shortform.repository;

import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
    List<ImageFile> findAllByChallenge(Challenge challenge);

    void deleteAllByChallenge(Challenge challenge);
}