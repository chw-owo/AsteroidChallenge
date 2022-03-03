package com.example.shortform.domain;

import com.example.shortform.dto.request.PostRequestDto;
import com.example.shortform.dto.resonse.CommentResponseDto;
import com.example.shortform.dto.resonse.PostIdResponseDto;
import com.example.shortform.dto.resonse.PostResponseDto;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(optional = false)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToOne(mappedBy = "post", orphanRemoval = true)
    private ImageFile imageFile;

    public void setImageFile(ImageFile imageFile) {
        this.imageFile = imageFile;
    }

    public PostIdResponseDto toResponse() {
        return PostIdResponseDto.builder()
                .postId(id)
                .build();
    }

    public void update(PostRequestDto requestDto) {
        this.content = requestDto.getContent();
    }

    public PostResponseDto toResponse(List<CommentResponseDto> commentList) {
        return PostResponseDto.builder()
                .postImage(imageFile.getFilePath())
                .postId(id)
                .content(content)
                .profileImage(user.getProfileImage())
                .nickname(user.getNickname())
                .comments(commentList)
                .build();
    }
}
