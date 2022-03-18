package com.example.shortform.domain;

import com.example.shortform.dto.resonse.CommentResponseDto;
import com.example.shortform.dto.resonse.CommentIdResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Comment extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public CommentIdResponseDto toPKResponse() {
        return CommentIdResponseDto.builder()
                .commentId(id)
                .build();
    }

    public CommentResponseDto toResponse() {
        return CommentResponseDto.builder()
                .commentId(id)
                .nickname(user.getNickname())
                .content(content)
                .levelName(user.getLevel().getName())
                .profileImage(user.getProfileImage())
                .build();
    }
}
