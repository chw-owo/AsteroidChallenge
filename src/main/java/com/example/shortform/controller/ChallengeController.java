package com.example.shortform.controller;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.domain.Category;
import com.example.shortform.domain.Challenge;
import com.example.shortform.dto.RequestDto.CategoryRequestDto;
import com.example.shortform.dto.RequestDto.ChallengeRequestDto;
import com.example.shortform.dto.ResponseDto.*;
import com.example.shortform.exception.InternalServerException;
import com.example.shortform.service.ChallengeService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.shortform.dto.request.ChallengeModifyRequestDto;
import com.example.shortform.dto.request.PasswordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;


import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@RequiredArgsConstructor
@RestController
public class ChallengeController {

    private final ChallengeService challengeService;

    //for test

    @PostMapping(value = "/challenge")
    public void postChallenge(@RequestPart("challenge") ChallengeRequestDto requestDto,
                              @AuthenticationPrincipal PrincipalDetails principal,
                              @RequestPart(value = "challengeImage", required = false) List<MultipartFile> multipartFiles) throws IOException, InternalServerException {
        challengeService.postChallenge(requestDto, principal, multipartFiles);
    }
//     @PostMapping("/challenge")
//     public ResponseEntity<?> createChallenge(@RequestPart(value = "imageFile", required = false) List<MultipartFile> multipartFileList,
//                                              @RequestPart("challenge")ChallengeRequestDto requestDto) throws IOException {
//         return challengeService.createChallenge(multipartFileList, requestDto);
//     }

    @GetMapping("/challenge")
    public List<ChallengesResponseDto> getChallenges() throws ParseException, InternalServerException {
        return challengeService.getChallenges();
    }

    @GetMapping("/challenge/{challengeId}")
    public ChallengeResponseDto getChallenge(@PathVariable Long challengeId) throws Exception, InternalServerException {
        return challengeService.getChallenge(challengeId);
    }

//     @GetMapping("/challenge/{challengeId}")
//     public ResponseEntity<?> getChallenge(@PathVariable Long challengeId) {
//         return challengeService.getChallenge(challengeId);
//     }

    @GetMapping("/challenge/category/{categoryId}")
    public List<ChallengesResponseDto> getCategoryChallenge(@PathVariable Category categoryId) throws ParseException, InternalServerException {
        return challengeService.getCategoryChallenge(categoryId);
    }

    @GetMapping("/challenge/search")
    public List<ChallengesResponseDto> getKeywordChallenge(@RequestParam("keyword") String keyword) throws ParseException, InternalServerException {
        return challengeService.getKeywordChallenge(keyword);
    }

    @PostMapping("/challenge/{challengeId}/user")
    public HashMap<String, Object> participateChallenge(@PathVariable Long challengeId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails != null) {
            challengeService.participateChallenge(challengeId, principalDetails);
            HashMap<String, Object> result = new HashMap<>();
            result.put("result", "true");
            return result;
        } else {
            throw new NullPointerException("로그인 후 이용가능합니다.");
        }
    }

    @PostMapping("/challenge/{challengeId}/private")
    public HashMap<String, Object> privateParticipateChallenge(@PathVariable Long challengeId, @RequestBody PasswordDto passwordDto, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails != null) {
            challengeService.privateParticipateChallenge(challengeId, passwordDto, principalDetails);
            HashMap<String, Object> result = new HashMap<>();
            result.put("result", "true");
            return result;
        } else {
            throw new NullPointerException("로그인 후 이용가능합니다.");
        }
    }

    @PutMapping("/challenge/{challengeId}")
    public ResponseEntity<?> modifyChallenge(@PathVariable Long challengeId,
                                             @RequestPart("challenge") ChallengeModifyRequestDto requestDto,
                                             @RequestPart(value = "challengeImage", required = false) List<MultipartFile> multipartFileList,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {
        if (principalDetails != null) {
            return challengeService.modifyChallenge(challengeId, requestDto, multipartFileList, principalDetails);
        } else {
            throw new NullPointerException("로그인 후 이용가능합니다.");
        }
    }

    @DeleteMapping("/challenge/{challengeId}/user")
    public void cancelChallenge(@PathVariable Long challengeId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails != null) {
            challengeService.cancelChallenge(challengeId, principalDetails);
        } else {
            throw new NullPointerException("로그인 후 이용가능합니다.");
        }
    }

    @GetMapping("/challenge/{challengeId}/report")
    public ReportResponseDto participateChallenge(@PathVariable Long challengeId) throws ParseException {// @AuthenticationPrincipal PrincipalDetails principalDetails) {
        //if (principalDetails != null) {
        return challengeService.successDate(challengeId);
//            HashMap<String, Object> result = new HashMap<>();
//            result.put("result", "true");
//            return result;
//        } else {
//            throw new NullPointerException("로그인 후 이용가능합니다.");
//        }

    }
}


