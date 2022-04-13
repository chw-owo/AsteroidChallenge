package com.example.shortform.controller;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.domain.Category;
import com.example.shortform.dto.RequestDto.ChallengeRequestDto;
import com.example.shortform.dto.RequestDto.ReportRequestDto;
import com.example.shortform.dto.ResponseDto.*;
import com.example.shortform.dto.request.ChallengeModifyRequestDto;
import com.example.shortform.dto.request.PasswordDto;
import com.example.shortform.dto.resonse.CMResponseDto;
import com.example.shortform.dto.resonse.ChallengeIdResponseDto;
import com.example.shortform.exception.InternalServerException;
import com.example.shortform.exception.NotFoundException;
import com.example.shortform.dto.ResponseDto.ChallengeResponseDto;
import com.example.shortform.dto.ResponseDto.ChallengesResponseDto;
import com.example.shortform.exception.UnauthorizedException;
import com.example.shortform.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class ChallengeController {

    private final ChallengeService challengeService;

    @PostMapping(value = "/challenge")

    public Map<String, Object> postChallenge(@RequestPart("challenge") ChallengeRequestDto requestDto,
                                             @AuthenticationPrincipal PrincipalDetails principal,
                                             @RequestPart(value = "challengeImage", required = false)
                                                         List<MultipartFile> multipartFiles) throws IOException, ParseException {
        if (principal != null) {
            HashMap<String, Object> result = new HashMap<>();
            result.put("result", "true");
            result.put("challengeId", challengeService.postChallenge(requestDto, principal, multipartFiles));
            return result;
        } else
            throw new UnauthorizedException("로그인 후 이용가능합니다.");

    }


    @GetMapping("/challenge")
    public ChallengePageResponseDto getChallenges(@RequestParam("page") int page,
                                                  @RequestParam("size") int size) throws ParseException {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        return challengeService.getChallenges(pageable);
    }

    @GetMapping("/challenge/{challengeId}")
    public ChallengeResponseDto getChallenge(@PathVariable Long challengeId) throws Exception, InternalServerException {
        return challengeService.getChallenge(challengeId);
    }

    @GetMapping("/challenge/category/{categoryId}")
    public ChallengePageResponseDto getCategoryChallenge(@PathVariable Long categoryId,
                                                            @RequestParam("page") int page,
                                                            @RequestParam("size") int size) throws ParseException {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        return challengeService.getCategoryChallenge(categoryId, pageable);
    }

    @GetMapping("/challenge/search")
    public ChallengePageResponseDto getKeywordChallenge(@RequestParam("keyword") String keyword,
                                                           @RequestParam("page") int page,
                                                           @RequestParam("size") int size) throws ParseException {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        return challengeService.getKeywordChallenge(keyword, pageable);
    }

    @PostMapping("/challenge/{challengeId}/user")
    public HashMap<String, Object> participateChallenge(@PathVariable Long challengeId,
                                                        @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails != null) {
            challengeService.participateChallenge(challengeId, principalDetails);
            HashMap<String, Object> result = new HashMap<>();
            result.put("result", "true");
            return result;
        } else {
            throw new UnauthorizedException("로그인 후 이용가능합니다.");
        }
    }

    @PostMapping("/challenge/{challengeId}/private")
    public HashMap<String, Object> privateParticipateChallenge(@PathVariable Long challengeId,
                                                               @RequestBody PasswordDto passwordDto,
                                                               @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails != null) {
            challengeService.privateParticipateChallenge(challengeId, passwordDto, principalDetails);
            HashMap<String, Object> result = new HashMap<>();
            result.put("result", "true");
            return result;
        } else {
            throw new UnauthorizedException("로그인 후 이용가능합니다.");
        }
    }

    @PatchMapping("/challenge/{challengeId}")
    public ResponseEntity<ChallengeIdResponseDto> modifyChallenge(@PathVariable Long challengeId,
                                                                  @RequestPart("challenge") ChallengeModifyRequestDto requestDto,
                                                                  @RequestPart(value = "challengeImage", required = false) List<MultipartFile> multipartFileList,
                                                                  @AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {
        if (principalDetails != null) {
            return challengeService.modifyChallenge(challengeId, requestDto, multipartFileList, principalDetails);
        } else {
            throw new UnauthorizedException("로그인 후 이용가능합니다.");
        }
    }

    @DeleteMapping("/challenge/{challengeId}/user")
    public ResponseEntity<CMResponseDto> cancelChallenge(@PathVariable Long challengeId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails != null) {
            return challengeService.cancelChallenge(challengeId, principalDetails);
        } else {
            throw new UnauthorizedException("로그인 후 이용가능합니다.");
        }
    }

    @PostMapping("/challenge/{challengeId}/report")
    public List<ReportResponseDto> getReport(@PathVariable Long challengeId, @RequestBody ReportRequestDto requestDto) {

        return challengeService.getReport(challengeId, requestDto);


    }

    @DeleteMapping("/challenge/{challengeId}")
    public ResponseEntity<CMResponseDto> deleteChallenge(@PathVariable Long challengeId,
                                                         @AuthenticationPrincipal PrincipalDetails principalDetails) throws ParseException {
        if (principalDetails != null)
            return challengeService.deleteChallenge(challengeId, principalDetails);
        else
            throw new NotFoundException("로그인 후 이용가능합니다.");
    }

    @GetMapping("/challenge/recommend/{challengeId}")
    public List<ChallengesResponseDto> recommendChallenge(@PathVariable Long challengeId,
                                                         @AuthenticationPrincipal PrincipalDetails principalDetails) throws ParseException {
        if (principalDetails != null)
            return challengeService.recommendChallenges(challengeId, principalDetails);
        else
            throw new NotFoundException("로그인 후 이용가능합니다.");
    }
}
