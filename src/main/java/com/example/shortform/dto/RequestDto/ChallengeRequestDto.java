package com.example.shortform.dto.RequestDto;

import com.example.shortform.domain.ImageFile;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@Getter
@Setter
public class ChallengeRequestDto {

    private String title;
    private String content;
    private MultipartFile challengeImage;
    private int maxMember;
    private String startDate; //LocalDate
    private String endDate; //LocalDate
    private Boolean isPrivate;
    private String password;
    private String category;
    private List<String> tagName;

}
