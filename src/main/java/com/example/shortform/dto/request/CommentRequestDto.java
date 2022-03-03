package com.example.shortform.dto.request;

import com.example.shortform.domain.Comment;
import com.example.shortform.domain.Post;
import com.example.shortform.domain.User;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto implements Serializable {
    private String content;

    public Comment toEntity(Post post, User user) {
        return Comment.builder()
                .user(user)
                .content(content)
                .post(post)
                .build();
    }
}
