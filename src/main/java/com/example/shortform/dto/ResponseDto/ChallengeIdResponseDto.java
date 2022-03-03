package com.example.shortform.dto.ResponseDto;

import com.example.shortform.domain.Category;
import com.example.shortform.domain.TagChallenge;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChallengeIdResponseDto {

    private Long challengeId;

}
