package com.example.shortform.service;

import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.Tag;
import com.example.shortform.domain.User;
import com.example.shortform.dto.ResponseDto.ChallengeResponseDto;
import com.example.shortform.dto.ResponseDto.MemberResponseDto;
import com.example.shortform.dto.ResponseDto.TagNameResponseDto;
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
            List<Tag> tagList = tagChallengeRepository.findAllByChallenge(challenge);
            for (Tag tag : tagList) {
                tagNameResponseDto = tag.toResponse();
                tagNameList.add(tagNameResponseDto);
            }
            List<User> userList = userChallengeRepository.findAllByChallenge(challenge);
            for (User user : userList) {
                memberList.add(user.toMemberResponse());
            }
            ChallengeResponseDto challengeResponseDto = challenge.toResponse(tagNameList, memberList);
            challengeResponseDtoList.add(challengeResponseDto);
        }

        return ResponseEntity.ok(challengeResponseDtoList);
    }
}
