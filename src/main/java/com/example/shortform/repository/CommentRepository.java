package com.example.shortform.repository;

import com.example.shortform.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "select c from Comment c " +
            "inner join c.post " +
            "inner join c.user " +
            "where c.post.id = :id",
            countQuery = "select count(c) from Comment c" +
                    " where c.post.id = :id")
    Page<Comment> findAllByPostId(Long id, Pageable pageable);

    @Query(value = "select c from Comment c " +
            "inner join c.post " +
            "inner join c.user " +
            "where c.post.id = :postId",
            countQuery = "select count(c) from Comment c " +
                    "inner join c.post " +
                    "where c.post.id = :postId")
    Page<Comment> findAllComment(Pageable pageable, Long postId);
}