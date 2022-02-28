package com.example.shortform.service;

import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.Tag;
import com.example.shortform.domain.TagChallenge;
import com.example.shortform.domain.User;
import com.example.shortform.dto.RequestDto.ChallengeModifyRequestDto;
import com.example.shortform.dto.ResponseDto.ChallengeResponseDto;
import com.example.shortform.dto.ResponseDto.MemberResponseDto;
import com.example.shortform.dto.ResponseDto.TagNameResponseDto;
import com.example.shortform.repository.ChallengeRepository;
import com.example.shortform.repository.TagChallengeRepository;
import com.example.shortform.repository.UserChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChallengeService {
    private final ChallengeRepository challengeRepository;
    private final TagChallengeRepository tagChallengeRepository;
    private final UserChallengeRepository userChallengeRepository;

    @Autowired
    public ChallengeService(ChallengeRepository challengeRepository,
                            TagChallengeRepository tagChallengeRepository,
                            UserChallengeRepository userChallengeRepository) {
        this.challengeRepository = challengeRepository;
        this.tagChallengeRepository = tagChallengeRepository;
        this.userChallengeRepository = userChallengeRepository;
    }

    @Transactional
    public ResponseEntity<?> getChallenge(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NullPointerException("찾는 챌린지가 존재하지 않습니다.")
        );

        List<Tag> tagList = tagChallengeRepository.findAllByChallenge(challenge);
        List<TagNameResponseDto> tagNameList = new ArrayList<>();

        for (Tag tag : tagList) {
            TagNameResponseDto responseDto = tag.toResponse();
            tagNameList.add(responseDto);
        }

        List<User> userList = userChallengeRepository.findAllByChallenge(challenge);
        List<MemberResponseDto> memberList = new ArrayList<>();

        for (User user : userList) {
            memberList.add(user.toMemberResponse());
        }

        ChallengeResponseDto challengeResponseDto = challenge.toResponse(tagNameList, memberList);
        return ResponseEntity.ok(challengeResponseDto);
    }

    public ResponseEntity<?> participateChallenge(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NullPointerException("찾는 챌린지가 존재하지 않습니다.")
        );

        List<User> userList = userChallengeRepository.findAllByChallenge(challenge);

//        if (challenge.getMaxMember() <= challenge.getCurrentMember()) {
//            throw new IllegalArgumentException("인원이 가득차 참여할 수 없습니다.");
//        } else {
//            if (challenge.getIsPrivate() == false) {
//
//            }
//        }
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    public ResponseEntity<?> modifyChallenge(Long challengeId, ChallengeModifyRequestDto requestDto) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NullPointerException("찾는 챌린지가 존재하지 않습니다.")
        );

        challenge.update(requestDto);

        List<Tag> tagList = tagChallengeRepository.findAllByChallenge(challenge);
        List<String> tagNames = requestDto.getTagName();

        for (Tag tag : tagList) {
            for (String tagName : tagNames) {
                tag.setName(tagName);
            }
        }

        return ResponseEntity.ok(challenge.toResponse());
    }

    public void cancelChallenge(Long challengeId) {
        challengeRepository.deleteById(challengeId);
    }
}
