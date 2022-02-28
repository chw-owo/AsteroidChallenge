package com.example.shortform.dto.RequestDto;

import com.example.shortform.domain.Comment;
import com.example.shortform.domain.Post;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto implements Serializable {
    private String content;

    public Comment toEntity(Post post) {
        return Comment.builder()
                .content(content)
                .post(post)
                .build();
    }
}
