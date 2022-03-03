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
public class ChallengeResponseDto {

    private Long challengeId;
    private Long userId;

    private String title;
    private String content;
    private List<String> challengeImage;
    private int maxMember;
    private int currentDate;
    private String startDate; //LocalDate
    private String endDate; //LocalDate
    private Boolean isPrivate;
    private String category;
    private List<String> tagChallenges;
    private String status;
    private List<String> members;

    public ChallengeResponseDto(Challenge challenge, List<String> challengeImage){
        this.challengeId = challenge.getId();
        this.title=challenge.getTitle();
        this.content=challenge.getContent();
        this.category= challenge.getCategory().getName();
        this.challengeImage= challengeImage;
        this.maxMember=challenge.getMaxMember();
        this.startDate=challenge.getStartDate();
        this.endDate=challenge.getEndDate();
        this.isPrivate=challenge.getIsPrivate();
        //this.status = challenge.getStatus();

        this.userId = null;
        this.members = null;

        List<String> tagChallengeStrings = new ArrayList<>();
        List<TagChallenge> tagChallenges = challenge.getTagChallenges();

        for(TagChallenge tagChallenge : tagChallenges){
            String tagChallengeString = tagChallenge.getTag().getName();
            tagChallengeStrings.add(tagChallengeString);
        }

        this.tagChallenges = tagChallengeStrings;
    }
}