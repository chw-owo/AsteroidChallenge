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
public class RankingRequestDto {

    private String nickname;
    private int rankingPoint;

}
