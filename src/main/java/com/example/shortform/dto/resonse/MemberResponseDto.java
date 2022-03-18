package com.example.shortform.dto.resonse;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponseDto {
    private Long userId;
    private String nickname;
    private String profileImage;
    private String levelName;
}
