package com.example.shortform.dto.RequestDto;

import lombok.*;

@Data
@Getter
@Setter
@Builder
public class CategoryRequestDto {
    private String name;

    @Builder
    public CategoryRequestDto (String name) {
        this.name = name;
    }
}
