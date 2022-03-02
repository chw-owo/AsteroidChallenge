package com.example.shortform.service;

import com.example.shortform.domain.*;
import com.example.shortform.dto.RequestDto.ChallengeModifyRequestDto;
import com.example.shortform.dto.RequestDto.ChallengeRequestDto;
import com.example.shortform.dto.ResponseDto.ChallengeResponseDto;
import com.example.shortform.dto.ResponseDto.MemberResponseDto;
import com.example.shortform.dto.ResponseDto.TagNameResponseDto;
import com.example.shortform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChallengeService {
    private final ChallengeRepository challengeRepository;
    private final TagChallengeRepository tagChallengeRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final ImageFileService imageFileService;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final ImageFileRepository imageFileRepository;

    @Autowired
    public ChallengeService(ChallengeRepository challengeRepository,
                            TagChallengeRepository tagChallengeRepository,
                            UserChallengeRepository userChallengeRepository,
                            ImageFileService imageFileService,
                            CategoryRepository categoryRepository,
                            TagRepository tagRepository,
                            ImageFileRepository imageFileRepository) {
        this.challengeRepository = challengeRepository;
        this.tagChallengeRepository = tagChallengeRepository;
        this.userChallengeRepository = userChallengeRepository;
        this.imageFileService = imageFileService;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.imageFileRepository = imageFileRepository;
    }

    @Transactional
    public ResponseEntity<?> getChallenge(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NullPointerException("찾는 챌린지가 존재하지 않습니다.")
        );

        List<TagChallenge> tagChallengeList = tagChallengeRepository.findAllByChallenge(challenge);
        List<TagNameResponseDto> tagNameList = new ArrayList<>();

        for (TagChallenge tagChallenge : tagChallengeList) {
            TagNameResponseDto responseDto = tagChallenge.getTag().toResponse();
            tagNameList.add(responseDto);
        }

        List<UserChallenge> userChallengeList = userChallengeRepository.findAllByChallenge(challenge);
        List<MemberResponseDto> memberList = new ArrayList<>();

        for (UserChallenge userChallenge : userChallengeList) {
            memberList.add(userChallenge.getUser().toMemberResponse());
        }

        List<ImageFile> imageFileList = imageFileRepository.findAllByChallenge(challenge);
        List<String> imagePathList = new ArrayList<>();
        for (ImageFile imageFile : imageFileList) {
            imagePathList.add(imageFile.getFilePath());
        }

        ChallengeResponseDto challengeResponseDto = challenge.toResponse(tagNameList, memberList, imagePathList);
        return ResponseEntity.ok(challengeResponseDto);
    }

    public ResponseEntity<?> participateChallenge(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NullPointerException("찾는 챌린지가 존재하지 않습니다.")
        );

        User user = new User();

        if (challenge.getMaxMember() <= challenge.getCurrentMember()) {
            throw new IllegalArgumentException("인원이 가득차 참여할 수 없습니다.");
        }

        userChallengeRepository.save(new UserChallenge(challenge, user));
        List<UserChallenge> userChallenges = userChallengeRepository.findAllByChallenge(challenge);
        challenge.setCurrentMember(userChallenges.size());

        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @Transactional
    public ResponseEntity<?> modifyChallenge(Long challengeId, ChallengeModifyRequestDto requestDto, List<MultipartFile> multipartFileList) throws IOException {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NullPointerException("찾는 챌린지가 존재하지 않습니다.")
        );

        List<ImageFile> imageFileList = imageFileService.uploadImage(multipartFileList, challenge);

        challenge.update(requestDto);

        List<TagChallenge> tagChallenges = tagChallengeRepository.findAllByChallenge(challenge);
        List<String> tagNames = requestDto.getTagName();

        int i = 0;

        for (TagChallenge tagChallenge : tagChallenges) {
            tagChallenge.getTag().setName(tagNames.get(i));
            i++;
        }

        return ResponseEntity.ok(challenge.toResponse());
    }

    public void cancelChallenge(Long challengeId) {
        challengeRepository.deleteById(challengeId);
    }

    public ResponseEntity<?> createChallenge(List<MultipartFile> multipartFileList, ChallengeRequestDto requestDto) throws IOException {
        Category category = categoryRepository.save(requestDto.toCategory());
        Challenge challenge = challengeRepository.save(requestDto.toEntity(category));
        List<String> tagNameList = requestDto.getTagName();
        for (String s : tagNameList) {
            Tag tag = new Tag(s);
            tagRepository.save(tag);
            tagChallengeRepository.save(new TagChallenge(challenge, tag));
        }

        List<ImageFile> imageFileList = imageFileService.uploadImage(multipartFileList, challenge);
        challenge.setImageFiles(imageFileList);
        return ResponseEntity.ok(challenge.toResponse());
    }
}
