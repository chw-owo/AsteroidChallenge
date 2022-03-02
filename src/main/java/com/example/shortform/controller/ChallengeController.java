package com.example.shortform.controller;

import com.example.shortform.domain.Category;
import com.example.shortform.domain.Challenge;
import com.example.shortform.dto.RequestDto.CategoryRequestDto;
import com.example.shortform.dto.RequestDto.ChallengeRequestDto;
import com.example.shortform.dto.ResponseDto.ChallengeIdResponseDto;
import com.example.shortform.dto.ResponseDto.ChallengeResponseDto;
import com.example.shortform.dto.ResponseDto.ChallengesResponseDto;
import com.example.shortform.dto.ResponseDto.TagResponseDto;
import com.example.shortform.service.ChallengeService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@RestController
public class ChallengeController {

    private final ChallengeService challengeService;

    //for test

    @PostMapping(value= "/challenge")
    public ChallengesResponseDto postChallenge(@RequestPart ChallengeRequestDto requestDto, @RequestPart MultipartFile multipartFile) throws IOException {
        return challengeService.postChallenge(requestDto, multipartFile);
    }

    @GetMapping("/challenge")
    public List<ChallengesResponseDto> getChallenges(){
        return challengeService.getChallenges();
    }

    @GetMapping("/challenge/{challengeId}")
    public ChallengeResponseDto getChallenge(@PathVariable Long challengeId) throws Exception {
        return challengeService.getChallenge(challengeId);
    }

    @GetMapping("/challenge/category/{categoryId}")
    public List<ChallengesResponseDto> getCategoryChallenge(@PathVariable Category categoryId ){
        return challengeService.getCategoryChallenge(categoryId);
    }

    @GetMapping("/challenge/search")
    public List<ChallengesResponseDto> getKeywordChallenge(@RequestParam("keyword") String keyword){
        return challengeService.getKeywordChallenge(keyword);
    }





}
