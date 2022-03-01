package com.example.shortform.dto.RequestDto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@Getter
@Setter
public class ChallengeRequestDto {

    private String title;
    private String content;
    private String challengeImage;
    private int maxMember;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isPrivate;
    private String password;
    private String category;
    private List<String> tagName;

}
