package com.example.shortform.dto.RequestDto;

import com.example.shortform.domain.*;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
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

}
