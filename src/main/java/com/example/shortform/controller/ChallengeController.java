package com.example.shortform.controller;

import com.example.shortform.dto.request.ChallengeModifyRequestDto;
import com.example.shortform.dto.request.ChallengeRequestDto;
import com.example.shortform.dto.request.PasswordDto;
import com.example.shortform.service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class ChallengeController {
    private final ChallengeService challengeService;

    @Autowired
    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity<?> getChallenge(@PathVariable Long challengeId) {
        return challengeService.getChallenge(challengeId);
    }

    @PostMapping("/challenge")
    public ResponseEntity<?> createChallenge(@RequestPart(value = "imageFile", required = false) List<MultipartFile> multipartFileList,
                                             @RequestPart("challenge")ChallengeRequestDto requestDto) throws IOException {
        return challengeService.createChallenge(multipartFileList, requestDto);
    }

    @PostMapping("/challenge/{challengeId}/user")
    public ResponseEntity<?> participateChallenge(@PathVariable Long challengeId) {
        return challengeService.participateChallenge(challengeId);
    }

    @PostMapping("/challenge/{challengeId}/private")
    public ResponseEntity<?> privateParticipateChallenge(@PathVariable Long challengeId, @RequestBody PasswordDto passwordDto) {
        return challengeService.privateParticipateChallenge(challengeId, passwordDto);
    }

    @PutMapping("/challenge/{challengeId}")
    public ResponseEntity<?> modifyChallenge(@PathVariable Long challengeId,
                                             @RequestPart("challenge") ChallengeModifyRequestDto requestDto,
                                             @RequestPart(value = "imageFile", required = false) List<MultipartFile> multipartFileList) throws IOException {
        return challengeService.modifyChallenge(challengeId, requestDto, multipartFileList);
    }

    @DeleteMapping("/challenge/{challengeId}/user")
    public void cancelChallenge(@PathVariable Long challengeId) {
        challengeService.cancelChallenge(challengeId);
    }
}
