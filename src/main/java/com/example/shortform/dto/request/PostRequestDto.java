package com.example.shortform.dto.request;

import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.Post;
import com.example.shortform.domain.User;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
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
