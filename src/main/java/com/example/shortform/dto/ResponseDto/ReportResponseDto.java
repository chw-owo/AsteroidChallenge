package com.example.shortform.dto.ResponseDto;

import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.TagChallenge;
import com.example.shortform.dto.resonse.MemberResponseDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReportResponseDto {

    private List<String> successDates;
}
