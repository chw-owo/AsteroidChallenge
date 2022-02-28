package com.example.shortform.controller;

import com.example.shortform.dto.RequestDto.ChallengeModifyRequestDto;
import com.example.shortform.service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/challenge/{challengeId}/user")
    public ResponseEntity<?> participateChallenge(@PathVariable Long challengeId) {
        return challengeService.participateChallenge(challengeId);
    }

    @PutMapping("/challenge/{challengeId}")
    public ResponseEntity<?> modifyChallenge(@PathVariable Long challengeId, @RequestBody ChallengeModifyRequestDto requestDto) {
        return challengeService.modifyChallenge(challengeId, requestDto);
    }

    @DeleteMapping("/challenge/{challengeId}/user")
    public void cancelChallenge(@PathVariable Long challengeId) {
        challengeService.cancelChallenge(challengeId);
    }
}
