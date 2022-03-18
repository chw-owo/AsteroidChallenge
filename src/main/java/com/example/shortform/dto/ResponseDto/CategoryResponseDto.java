package com.example.shortform.dto.ResponseDto;

import lombok.*;

import java.util.*;

@Data
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CategoryResponseDto {
    private List<Long> categoryId;

    public CategoryResponseDto(List<Long> categoryId) {
        this.categoryId = categoryId;
    }
}
