package com.example.shortform.dto.request;

import com.example.shortform.domain.Category;
import com.example.shortform.domain.Challenge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChallengeRequestDto {
    private String title;
    private String content;
    private int maxMember;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isPrivate = false;
    private String password;
    private String category;
    private List<String> tagName;

    public Challenge toEntity(Category category) {
        return Challenge.builder()
                .title(title)
                .content(content)
                .startDate(startDate)
                .endDate(endDate)
                .isPrivate(isPrivate)
                .password(password)
                .category(category)
                .maxMember(maxMember)
                .build();
    }

    public Category toCategory() {
        return Category.builder()
                .name(category)
                .build();
    }
}
