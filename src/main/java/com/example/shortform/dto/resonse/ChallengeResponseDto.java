package com.example.shortform.dto.resonse;

import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.TagChallenge;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChallengeResponseDto {

    private Long challengeId;
    private Long userId;
    private String roomId;
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
    private List<MemberResponseDto> members;

    public ChallengeResponseDto(Challenge challenge, List<String> challengeImage){
        this.challengeId = challenge.getId();
        this.title=challenge.getTitle();
        this.content=challenge.getContent();
        this.category= challenge.getCategory().getName();
        this.challengeImage= challengeImage;
        this.currentMember = challenge.getCurrentMember();
        this.maxMember=challenge.getMaxMember();
        this.startDate=challenge.getStartDate();
        this.endDate=challenge.getEndDate();
        this.isPrivate=challenge.getIsPrivate();
        this.roomId = challenge.getChatRoom().getId().toString();
        this.userId = challenge.getUser().getId();

        List<String> tagChallengeStrings = new ArrayList<>();
        List<TagChallenge> tagChallenges = challenge.getTagChallenges();

        for(TagChallenge tagChallenge : tagChallenges){
            String tagChallengeString = tagChallenge.getTag().getName();
            tagChallengeStrings.add(tagChallengeString);
        }

        this.tagName = tagChallengeStrings;
    }
}
