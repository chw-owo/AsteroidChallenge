package com.example.shortform.controller;

import com.example.shortform.domain.Challenge;
import com.example.shortform.dto.RequestDto.CategoryRequestDto;
import com.example.shortform.dto.RequestDto.ChallengeRequestDto;
import com.example.shortform.dto.ResponseDto.ChallengeIdResponseDto;
import com.example.shortform.dto.ResponseDto.ChallengeResponseDto;
import com.example.shortform.dto.ResponseDto.TagResponseDto;
import com.example.shortform.service.ChallengeService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RequiredArgsConstructor
@RestController
public class ChallengeController {

    private final ChallengeService challengeService;

    //for test

    @PostMapping("/challenge")
    public ChallengeResponseDto postChallenge(@RequestBody ChallengeRequestDto requestDto){
        return challengeService.postChallenge(requestDto);
    }

    @GetMapping("/challenge")
    public List<ChallengeResponseDto> getChallenge(){
        return challengeService.getChallenge();
    }

    @GetMapping("/challenge/recommend")
    public List<TagResponseDto> getRecommendChallenge(){
        return challengeService.getRecommendChallenge();
    }


}
