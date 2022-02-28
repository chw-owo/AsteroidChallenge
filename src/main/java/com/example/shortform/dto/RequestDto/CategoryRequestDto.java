package com.example.shortform.dto.RequestDto;

import lombok.*;

@Data
@NoArgsConstructor
@Getter
@Setter
public class CategoryRequestDto {
    private String name;
    public CategoryRequestDto (String name) {
        this.name = name;
    }
}
