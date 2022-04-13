package com.example.shortform.dto.resonse;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TagResponseDto {

    private List<String> names;
}
