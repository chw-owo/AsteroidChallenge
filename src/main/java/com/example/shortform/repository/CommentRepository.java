package com.example.shortform.repository;

import com.example.shortform.domain.Comment;
import com.example.shortform.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostIdOrderByCreatedAtDesc(Long id);
    ;
    List<Comment> findAllByPostId(Long id, PageRequest pageRequest);

    Page<Comment> findAllByPostId(PageRequest pageRequest, Long postId);
}