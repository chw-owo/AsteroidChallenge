package com.example.shortform.service;

import com.example.shortform.domain.*;
import com.example.shortform.dto.resonse.ChallengeResponseDto;
import com.example.shortform.dto.resonse.MemberResponseDto;
import com.example.shortform.dto.resonse.TagNameResponseDto;
import com.example.shortform.repository.ChallengeRepository;
import com.example.shortform.repository.TagChallengeRepository;
import com.example.shortform.repository.UserChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {
    private final ChallengeRepository challengeRepository;
    private final TagChallengeRepository tagChallengeRepository;
    private final UserChallengeRepository userChallengeRepository;

    @Autowired
    public SearchService(ChallengeRepository challengeRepository,
                         TagChallengeRepository tagChallengeRepository,
                         UserChallengeRepository userChallengeRepository) {
        this.challengeRepository = challengeRepository;
        this.tagChallengeRepository = tagChallengeRepository;
        this.userChallengeRepository = userChallengeRepository;
    }

    public ResponseEntity<?> searchChallenge(String search) {
        List<Challenge> challengeList = challengeRepository.findAllByTitleContaining(search);

        List<TagNameResponseDto> tagNameList = new ArrayList<>();
        List<MemberResponseDto> memberList = new ArrayList<>();
        List<ChallengeResponseDto> challengeResponseDtoList = new ArrayList<>();
        TagNameResponseDto tagNameResponseDto;

        for (Challenge challenge : challengeList) {
            List<TagChallenge> tagChallengeList = tagChallengeRepository.findAllByChallenge(challenge);
            for (TagChallenge tagChallenge : tagChallengeList) {
                tagNameResponseDto = tagChallenge.getTag().toResponse();
                tagNameList.add(tagNameResponseDto);
            }
            List<UserChallenge> userList = userChallengeRepository.findAllByChallenge(challenge);
            for (UserChallenge userChallenge : userList) {
                memberList.add(userChallenge.getUser().toMemberResponse());
            }

            ChallengeResponseDto challengeResponseDto = challenge.toSearchResponse(tagNameList, memberList);
            challengeResponseDtoList.add(challengeResponseDto);
        }

        return ResponseEntity.ok(challengeResponseDtoList);
    }
}
