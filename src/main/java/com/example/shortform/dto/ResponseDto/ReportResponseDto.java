package com.example.shortform.dto.ResponseDto;

import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.TagChallenge;
import com.example.shortform.domain.User;
import com.example.shortform.domain.UserChallenge;
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
    //private List<String> successDates;
    private String date;
    private int percentage;
}
