package com.example.shortform.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDetailResponseDto {
    private Long commentId;
    private String nickname;
    private String content;
    private String profileImage;
    private String createdAt;

    public CommentDetailResponseDto setCreatedAt(String createdAt) {
        return CommentDetailResponseDto.builder()
                .createdAt(createdAt)
                .build();
    }
}
