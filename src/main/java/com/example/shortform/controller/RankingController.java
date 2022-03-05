package com.example.shortform.controller;
import com.example.shortform.domain.User;
import com.example.shortform.dto.RequestDto.RankingRequestDto;
import com.example.shortform.dto.ResponseDto.ChallengesResponseDto;
import com.example.shortform.dto.ResponseDto.RankingResponseDto;
import com.example.shortform.service.ChallengeService;
import com.example.shortform.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@RestController
public class RankingController {

    private final RankingService rankingService;

    /*@PostMapping("/ranking")
    public int setRanking(@RequestBody RankingRequestDto requestDto){
        return rankingService.setRanking(requestDto);
    }*/

    @GetMapping("/ranking")
    public List<RankingResponseDto> getRanking(){
        return rankingService.getRanking();
    }

}
