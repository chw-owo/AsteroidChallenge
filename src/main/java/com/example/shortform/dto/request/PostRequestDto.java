package com.example.shortform.dto.request;

import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.Post;
import com.example.shortform.domain.User;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostRequestDto {
    private String content;

    public Post toEntity(Challenge challenge, User user) {
        return Post.builder()
                .user(user)
                .challenge(challenge)
                .content(content)
                .build();
    }
}
