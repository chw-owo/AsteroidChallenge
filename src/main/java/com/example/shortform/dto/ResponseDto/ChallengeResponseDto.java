package com.example.shortform.dto.ResponseDto;

import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.ImageFile;
import com.example.shortform.domain.TagChallenge;
import com.example.shortform.dto.resonse.MemberResponseDto;
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
    private int currentMember;
    private String startDate; //LocalDate
    private String endDate; //LocalDate
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
        this.maxMember=challenge.getMaxMember();
        this.startDate=challenge.getStartDate();
        this.endDate=challenge.getEndDate();
        this.isPrivate=challenge.getIsPrivate();

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
