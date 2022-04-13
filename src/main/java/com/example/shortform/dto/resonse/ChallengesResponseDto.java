package com.example.shortform.dto.resonse;

import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.TagChallenge;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChallengesResponseDto {

    private Long challengeId;

    private String title;
    private String content;
    private List<String> challengeImage;
    private int maxMember;
    private int currentMember;
    private String startDate;
    private String endDate;
    private Boolean isPrivate;
    private String category;
    private List<String> tagName;
    private String status;

    public ChallengesResponseDto(Challenge challenge, List<String> challengeImage){
        this.challengeId = challenge.getId();
        this.title=challenge.getTitle();
        this.content=challenge.getContent();
        this.category= challenge.getCategory().getName();
        this.challengeImage=challengeImage;
        this.currentMember = challenge.getCurrentMember();
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

        this.tagName = tagChallengeStrings;
    }

}
