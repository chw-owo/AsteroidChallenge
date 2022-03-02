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
@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;
    private final TagChallengeRepository tagChallengeRepository;
    private final ImageFileService imageFileService;

    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public ChallengesResponseDto postChallenge(ChallengeRequestDto requestDto,
     MultipartFile multipartFile) throws IOException {

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
        ImageFile imageFileUpload = imageFileService.upload(multipartFile, challenge);
        challenge.setChallengeImage(imageFileUpload);
        ChallengesResponseDto responseDto = new ChallengesResponseDto(challenge);

        // 날짜 받기 근데 무슨 형식으로 받지...?
        // 유저 등록, 매니저 등록 => 로그인이랑 합치고 하자

        return responseDto;

    }

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
        ChallengeResponseDto challengeResponseDtos = new ChallengeResponseDto(challenge);
        return challengeResponseDtos;
    }

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


}
