package com.example.shortform.controller;
import com.example.shortform.dto.ResponseDto.ChallengesResponseDto;
import com.example.shortform.dto.ResponseDto.RankingResponseDto;
import com.example.shortform.service.ChallengeService;
import com.example.shortform.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RequiredArgsConstructor
@RestController
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/ranking")
    public List<RankingResponseDto> getRanking(){
        return rankingService.getRanking();
    }

}
