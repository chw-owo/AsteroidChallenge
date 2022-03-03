package com.example.shortform.service;

import com.example.shortform.domain.*;
import com.example.shortform.dto.RequestDto.CategoryRequestDto;
import com.example.shortform.dto.RequestDto.ChallengeRequestDto;
import com.example.shortform.dto.ResponseDto.ChallengeIdResponseDto;
import com.example.shortform.dto.ResponseDto.ChallengeResponseDto;
import com.example.shortform.dto.ResponseDto.ChallengesResponseDto;
import com.example.shortform.dto.ResponseDto.TagResponseDto;
import com.example.shortform.repository.CategoryRepository;
import com.example.shortform.repository.ChallengeRepository;
import com.example.shortform.repository.TagChallengeRepository;
import com.example.shortform.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

import com.example.shortform.dto.request.ChallengeModifyRequestDto;
import com.example.shortform.dto.request.PasswordDto;
import com.example.shortform.dto.resonse.MemberResponseDto;
import com.example.shortform.dto.resonse.TagNameResponseDto;
import com.example.shortform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;
    private final TagChallengeRepository tagChallengeRepository;
    private final ImageFileService imageFileService;
  
  
    private final UserChallengeRepository userChallengeRepository;
    private final ImageFileRepository imageFileRepository;


    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public ChallengeResponseDto postChallenge(ChallengeRequestDto requestDto,
     List<MultipartFile> multipartFiles) throws IOException {

        // 카테고리 받아오기
        Category category = categoryRepository.findByName(requestDto.getCategory());

        // 태그 저장하기
        List<TagChallenge> tagChallenges = new ArrayList<>();
        Challenge challenge =  new Challenge(requestDto, category);
        List<String> tagStrings = requestDto.getTagName();
        for(String tagString:tagStrings){
            Tag tag = new Tag(tagString);
            tagRepository.save(tag);

            TagChallenge tagChallenge = new TagChallenge(challenge, tag);
            if (!tagChallenges.contains(tagChallenge)){ // 한 게시물 내부의 중복태그 방지
                tagChallenges.add(tagChallenge);
                tagChallengeRepository.save(tagChallenge);
            }
        }
        challenge.setTagChallenges(tagChallenges);

        //챌린지 저장
        challengeRepository.save(challenge);

        // 방 비밀번호 암호화
        String encPassword = passwordEncoder.encode(requestDto.getPassword());
        challenge.setPassword(encPassword);

        // 이미지 업로드
        List<String> challengeImages = new ArrayList<>();
        for(MultipartFile m : multipartFiles){
            ImageFile imageFileUpload = imageFileService.upload(m, challenge);
            challengeImages.add(imageFileUpload.getFilePath());
        }

        ChallengeResponseDto responseDto = new ChallengeResponseDto(challenge,challengeImages);

        return responseDto;

    }
  
//     public ResponseEntity<?> createChallenge(List<MultipartFile> multipartFileList, ChallengeRequestDto requestDto) throws IOException {
//         Category category = categoryRepository.save(requestDto.toCategory());
//         Challenge challenge = challengeRepository.save(requestDto.toEntity(category));
//         List<String> tagNameList = requestDto.getTagName();
//         for (String s : tagNameList) {
//             Tag tag = new Tag(s);
//             tagRepository.save(tag);
//             tagChallengeRepository.save(new TagChallenge(challenge, tag));
//         }

//        List<ImageFile> imageFileList = imageFileService.uploadImage(multipartFileList, challenge);
//        challenge.setImageFiles(imageFileList);
//        return ResponseEntity.ok(challenge.toResponse());
//    }

    public List<ChallengesResponseDto> getChallenges(){
        List<Challenge> challenges = challengeRepository.findAllByOrderByCreatedAt();
        List<ChallengesResponseDto> challengesResponseDtos = new ArrayList<>();

        for(Challenge challenge: challenges){
            ChallengesResponseDto responseDto = new ChallengesResponseDto(challenge);
            challengesResponseDtos.add(responseDto);
        }

        return challengesResponseDtos;
    }

    public ChallengeResponseDto getChallenge(Long challengeId) throws Exception {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(() -> new Exception());
        List<String> challengeImage = new ArrayList<>();
        ChallengeResponseDto challengeResponseDtos = new ChallengeResponseDto(challenge, challengeImage);
        return challengeResponseDtos;
    }
  
//   @Transactional
//     public ResponseEntity<?> getChallenge(Long challengeId) {
//         Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
//                 () -> new NullPointerException("찾는 챌린지가 존재하지 않습니다.")
//         );

//         List<TagChallenge> tagChallengeList = tagChallengeRepository.findAllByChallenge(challenge);
//         List<TagNameResponseDto> tagNameList = new ArrayList<>();

//         for (TagChallenge tagChallenge : tagChallengeList) {
//             TagNameResponseDto responseDto = tagChallenge.getTag().toResponse();
//             tagNameList.add(responseDto);
//         }

//         List<UserChallenge> userChallengeList = userChallengeRepository.findAllByChallenge(challenge);
//         List<MemberResponseDto> memberList = new ArrayList<>();

//         for (UserChallenge userChallenge : userChallengeList) {
//             memberList.add(userChallenge.getUser().toMemberResponse());
//         }

//         List<ImageFile> imageFileList = imageFileRepository.findAllByChallenge(challenge);
//         List<String> imagePathList = new ArrayList<>();
//         for (ImageFile imageFile : imageFileList) {
//             imagePathList.add(imageFile.getFilePath());
//         }

//         ChallengeResponseDto challengeResponseDto = challenge.toResponse(tagNameList, memberList, imagePathList);
//         return ResponseEntity.ok(challengeResponseDto);
//     }

    public List<ChallengesResponseDto> getCategoryChallenge(Category categoryId){
        List<Challenge> challenges = challengeRepository.findAll();
        List<ChallengesResponseDto> ChallengesResponseDtos = new ArrayList<>();

        for(Challenge challenge: challenges){
            if(categoryId.equals(challenge.getCategory())){
                ChallengesResponseDto responseDto = new ChallengesResponseDto(challenge);
                ChallengesResponseDtos.add(responseDto);
            }
        }

        return ChallengesResponseDtos;
    }

    public List<ChallengesResponseDto> getKeywordChallenge(String keyword){
        List<Challenge> challenges = challengeRepository.findAll();
        List<ChallengesResponseDto> ChallengesResponseDtos = new ArrayList<>();

        for(Challenge c: challenges){
            if(c.getTitle().contains(keyword)){
                ChallengesResponseDto responseDto = new ChallengesResponseDto(c);
                ChallengesResponseDtos.add(responseDto);
            }
            for(TagChallenge t : c.getTagChallenges()){
                if(t.getTag().getName().contains(keyword)){
                    ChallengesResponseDto responseDto = new ChallengesResponseDto(c);
                    ChallengesResponseDtos.add(responseDto);
                }
            }
        }

        return ChallengesResponseDtos;
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

    

    public ResponseEntity<?> privateParticipateChallenge(Long challengeId, PasswordDto passwordDto) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NullPointerException("찿는 챌린지가 존재하지 않습니다.")
        );

        if (challenge.getPassword().equals(passwordDto.getPassword())) {
            return ResponseEntity.ok(true);
        } else {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다");
        }
    }

}
