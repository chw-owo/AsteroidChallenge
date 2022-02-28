package com.example.shortform.domain;

import com.example.shortform.dto.RequestDto.PostRequestDto;
import com.example.shortform.dto.ResponseDto.CommentDetailResponseDto;
import com.example.shortform.dto.ResponseDto.PostIdResponseDto;
import com.example.shortform.dto.ResponseDto.PostResponseDto;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Post extends Timestamped{
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "post_image")
    private String postImage;

    @ManyToOne(optional = false)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public PostIdResponseDto toResponse() {
        return PostIdResponseDto.builder()
                .postId(id)
                .build();
    }

    public void update(PostRequestDto requestDto) {
        this.content = requestDto.getContent();
        this.postImage = requestDto.getPostImage();
    }

    public PostResponseDto toResponse(List<CommentDetailResponseDto> commentList) {
        return PostResponseDto.builder()
                .content(content)
                .postImage(postImage)
                .profileImage(user.getProfileImage())
                .nickname(user.getNickname())
                .comments(commentList)
                .build();
    }
}
