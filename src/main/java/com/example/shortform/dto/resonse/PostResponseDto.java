package com.example.shortform.dto.resonse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponseDto {
    private Long postId;
    private String nickname;
    private String profileImage;
    private String levelName;
    private String postImage;
    private String content;
    private String createdAt;
    private long commentCnt;
    private List<CommentResponseDto> comments;

}
