package com.example.shortform.dto.request;

import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.Post;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostRequestDto {
    private String content;

    public Post toEntity(Challenge challenge) {
        return Post.builder()
                .challenge(challenge)
                .content(content)
                .build();
    }
}
