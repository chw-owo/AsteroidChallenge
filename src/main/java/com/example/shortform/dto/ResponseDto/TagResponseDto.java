package com.example.shortform.dto.ResponseDto;

import lombok.*;

import java.util.*;
import java.nio.file.FileStore;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TagResponseDto {
    private List<String> names;
}
