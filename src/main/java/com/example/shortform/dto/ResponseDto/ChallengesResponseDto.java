package com.example.shortform.dto.ResponseDto;

import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.ImageFile;
import com.example.shortform.domain.TagChallenge;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChallengesResponseDto {

    private Long challengeId;

    private String title;
    private String content;
    private String challengeImage;
    private int maxMember;
    private int currentDate;
    private String startDate; //LocalDate
    private String endDate; //LocalDate
    private Boolean isPrivate;
    private String category;
    private List<String> tagChallenges;
    private String status;

    public ChallengesResponseDto(Challenge challenge){
        this.challengeId = challenge.getId();
        this.title=challenge.getTitle();
        this.content=challenge.getContent();
        this.category= challenge.getCategory().getName();
        this.challengeImage=challenge.getChallengeImage().getFilePath();
        this.maxMember=challenge.getMaxMember();
        this.startDate=challenge.getStartDate();
        this.endDate=challenge.getEndDate();
        this.isPrivate=challenge.getIsPrivate();

        List<String> tagChallengeStrings = new ArrayList<>();
        List<TagChallenge> tagChallenges = challenge.getTagChallenges();

        for(TagChallenge tagChallenge : tagChallenges){
            String tagChallengeString = tagChallenge.getTag().getName();
            tagChallengeStrings.add(tagChallengeString);
        }

        this.tagChallenges = tagChallengeStrings;
    }
}
