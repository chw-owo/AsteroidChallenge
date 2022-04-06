package com.example.shortform.repository;

import com.example.shortform.domain.Comment;
import com.example.shortform.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostIdOrderByCreatedAtDesc(Long id);

    @Query(value = "select c from Comment c inner join c.post inner join c.user where c.post.id = :id",
    countQuery = "select count(c) from Comment c where c.post.id = :id")
    Page<Comment> findAllByPostId(Long id, Pageable pageable);

    Page<Comment> findAllByPostId(Pageable pageable, Long postId);

    @Query(value = "select c from Comment c inner join c.post inner join c.user where c.post.id = :postId",
    countQuery = "select count(c) from Comment c inner join c.post where c.post.id = :postId")
    Page<Comment> findAllComment(Pageable pageable, Long postId);
}