package com.example.shortform.dto.ResponseDto;

import com.example.shortform.domain.Category;
import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.TagChallenge;
import com.example.shortform.dto.RequestDto.ChallengeRequestDto;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChallengeResponseDto {

    private Long challengeId;

    private String title;
    private String content;
    private String challengeImage;
    private int maxMember;
    private int currentDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isPrivate;
    private String category;
    private List<TagChallenge> tagChallenges;
    private String status;

    public ChallengeResponseDto(Challenge challenge){
        this.title=challenge.getTitle();
        this.content=challenge.getContent();
        this.category= challenge.getCategory().getName();
        this.challengeImage=challenge.getChallengeImage();
        this.maxMember=challenge.getMaxMember();
        this.startDate=challenge.getStartDate();
        this.endDate=challenge.getEndDate();
        this.isPrivate=challenge.getIsPrivate();
        this.tagChallenges=null;//tagChallenges;

    }
}
