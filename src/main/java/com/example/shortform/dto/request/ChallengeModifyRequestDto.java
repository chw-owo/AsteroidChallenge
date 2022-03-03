package com.example.shortform.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChallengeModifyRequestDto {
    private String title;
    private String content;
    private String category;
    private List<String> tagName;
}
