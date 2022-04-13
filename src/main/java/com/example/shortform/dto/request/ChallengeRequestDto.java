package com.example.shortform.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@Getter
@Setter
public class ChallengeRequestDto {

    private String title;
    private String content;
    private List<MultipartFile> challengeImages;
    private int maxMember;
    private String startDate;
    private String endDate;
    private Boolean isPrivate;
    private String password;
    private String category;
    private List<String> tagName;

}
