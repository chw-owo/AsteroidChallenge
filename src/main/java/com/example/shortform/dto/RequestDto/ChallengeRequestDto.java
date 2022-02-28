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
    private List<String> tagChallenges;

    public ChallengeRequestDto(

    String title,
    String content,
    String challengeImage,
    int maxMember,
    LocalDate startDate,
    LocalDate endDate,
    Boolean isPrivate,
    String password,
    String category

    ){
        this.title=title;
        this.content=content;
        this.category= category;
        this.challengeImage=challengeImage;
        this.maxMember=maxMember;
        this.startDate=startDate;
        this.endDate=endDate;
        this.isPrivate=isPrivate;
        this.password=password;
        this.tagChallenges=null;

    }
}
