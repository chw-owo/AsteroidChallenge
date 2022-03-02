package com.example.shortform.dto.request;

import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.ImageFile;
import com.example.shortform.domain.Post;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageFileRequestDto {
    private String originalFileName;
    private String convertedFileName;
    private String filePath;
    private Long fileSize;

    public ImageFile toEntity(Challenge challenge) {
        return ImageFile.builder()
                .originalFileName(this.originalFileName)
                .convertedFileName(this.convertedFileName)
                .filePath(this.filePath)
                .fileSize(this.fileSize)
                .challenge(challenge)
                .build();
    }

    public ImageFile toEntity(Post post) {
        return ImageFile.builder()
                .originalFileName(this.originalFileName)
                .convertedFileName(this.convertedFileName)
                .filePath(this.filePath)
                .fileSize(this.fileSize)
                .post(post)
                .build();
    }
}
