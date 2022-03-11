package com.example.shortform.controller;
import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.domain.User;
import com.example.shortform.dto.RequestDto.RankingRequestDto;
import com.example.shortform.dto.ResponseDto.ChallengesResponseDto;
import com.example.shortform.dto.ResponseDto.RankingResponseDto;
import com.example.shortform.exception.NotFoundException;
import com.example.shortform.service.ChallengeService;
import com.example.shortform.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@RestController
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/ranking")
    public List<RankingResponseDto> getRanking(@AuthenticationPrincipal PrincipalDetails principalDetails){

        if (principalDetails == null)
            throw new NotFoundException("로그인한 유저정보가 없습니다.");

        return rankingService.getRanking(principalDetails);
    }

}
