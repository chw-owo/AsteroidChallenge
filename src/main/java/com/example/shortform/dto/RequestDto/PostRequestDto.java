package com.example.shortform.dto.RequestDto;

import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.Post;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostRequestDto implements Serializable {
    private String content;
    private String postImage;

    public Post toEntity(Challenge challenge) {
        return Post.builder()
                .challenge(challenge)
                .postImage(postImage)
                .content(content)
                .build();
    }
}
