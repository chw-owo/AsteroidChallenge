package com.example.shortform.repository;

import com.example.shortform.domain.ImageFile;
import com.example.shortform.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {

    ImageFile findByPost(Post post);
}