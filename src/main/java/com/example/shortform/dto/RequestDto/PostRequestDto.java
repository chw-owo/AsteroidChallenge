package com.example.shortform.dto.RequestDto;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostRequestDto implements Serializable {
    private String content;
    private String postImage;
}
