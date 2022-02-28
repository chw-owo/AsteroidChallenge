package com.example.shortform.dto.request;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentRequestDto implements Serializable {
    private String content;
}
