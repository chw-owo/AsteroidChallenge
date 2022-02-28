package com.example.shortform.dto.ResponseDto;

import com.example.shortform.domain.Tag;
import com.example.shortform.domain.User;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ChallengeResponseDto implements Serializable {
    private Long challengeId;
    private Long userId;
    private String title;
    private String content;
    private String challengeImage;
    private int maxMember;
    private int currentMember;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isPrivate = false;
    private String categoryName;
    private List<TagNameResponseDto> tagNameList;
    private List<MemberResponseDto> members;
    private String status;
}
