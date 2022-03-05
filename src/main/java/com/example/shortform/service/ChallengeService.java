package com.example.shortform.service;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.domain.*;
import com.example.shortform.dto.RequestDto.ChallengeRequestDto;
import com.example.shortform.dto.ResponseDto.ChallengeResponseDto;
import com.example.shortform.dto.ResponseDto.ChallengesResponseDto;
import com.example.shortform.dto.resonse.MemberResponseDto;

import com.example.shortform.exception.DuplicateException;
import com.example.shortform.exception.InternalServerException;
import com.example.shortform.exception.NotFoundException;
import com.example.shortform.repository.CategoryRepository;
import com.example.shortform.repository.ChallengeRepository;
import com.example.shortform.repository.TagChallengeRepository;
import com.example.shortform.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.omg.CORBA.DynAnyPackage.Invalid;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.transaction.Transactional;
import java.awt.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import com.example.shortform.dto.request.ChallengeModifyRequestDto;
import com.example.shortform.dto.request.PasswordDto;
import com.example.shortform.repository.*;
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
    private final UserRepository userRepository;
    private final ImageFileRepository imageFileRepository;


    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void postChallenge(ChallengeRequestDto requestDto,
                                              PrincipalDetails principal,
                                            List<MultipartFile> multipartFiles) throws IOException, InternalServerException {

        // 카테고리 받아오기

        Category category = categoryRepository.findByName(requestDto.getCategory());

        // 태그 저장하기
        List<TagChallenge> tagChallenges = new ArrayList<>();
        Challenge challenge = new Challenge(requestDto, category);
        List<String> tagStrings = requestDto.getTagName();

        for (String tagString : tagStrings) {
            Tag tag = new Tag(tagString);
            tagRepository.save(tag);

            TagChallenge tagChallenge = new TagChallenge(challenge, tag);
            if (!tagChallenges.contains(tagChallenge)) { // 한 게시물 내부의 중복태그 방지
                tagChallenges.add(tagChallenge);
                tagChallengeRepository.save(tagChallenge);
            } else {
                throw new DuplicateException("중복된 태그는 사용할 수 없습니다.");
            }
        }
        challenge.setTagChallenges(tagChallenges);

        //챌린지 저장
        challengeRepository.save(challenge);

        // 방 비밀번호 암호화
        if (requestDto.getPassword() != null) {
            String encPassword = passwordEncoder.encode(requestDto.getPassword());
            challenge.setPassword(encPassword);
        }

        // 이미지 업로드

        List<ImageFile> imageFileList = new ArrayList<>();
        List<String> challengeImages = new ArrayList<>();
        if (multipartFiles.isEmpty()) {
            throw new InternalServerException("한장 이상의 이미지를 업로드 해야합니다.");
        }
        for (MultipartFile m : multipartFiles) {
            ImageFile imageFileUpload = imageFileService.upload(m, challenge);
            imageFileList.add(imageFileUpload);
            challengeImages.add(imageFileUpload.getFilePath());
        }
        challenge.setChallengeImage(imageFileList);

        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        challenge.setUser(user);
        UserChallenge userChallenge = new UserChallenge(challenge, user);
        userChallengeRepository.save(userChallenge);
        challenge.setUser(user);


    }

    public String challengeStatus(Challenge challenge) throws ParseException {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = dateFormat.parse(challenge.getStartDate());
        Date endDate = dateFormat.parse(challenge.getEndDate());

        if(now.getTime() < startDate.getTime()){
            challenge.setStatus(ChallengeStatus.BEFORE);
            return "모집중";

        }else if (startDate.getTime() <= now.getTime() && now.getTime() <= endDate.getTime()){
            challenge.setStatus(ChallengeStatus.ING);
            return "진행중";
        }else{
            //이 부분은 따로 설정
            challenge.setStatus(ChallengeStatus.SUCCESS);
            return "완료";
        }
    }

    public List<ChallengesResponseDto> getChallenges() throws ParseException, InternalServerException {
        List<Challenge> challenges = challengeRepository.findAllByOrderByCreatedAt();
        List<ChallengesResponseDto> challengesResponseDtos = new ArrayList<>();


        for(Challenge challenge: challenges){
            List<String> challengeImages = new ArrayList<>();
            List<ImageFile> ImageFiles =  challenge.getChallengeImage();
            if(ImageFiles.isEmpty()){
                throw new InternalServerException("챌린지 이미지를 찾을 수 없습니다.");
            }
            for(ImageFile image:ImageFiles){
                challengeImages.add(image.getFilePath());
            }
            String challengeStatus = challengeStatus(challenge);
            ChallengesResponseDto responseDto = new ChallengesResponseDto(challenge, challengeImages);
            responseDto.setStatus(challengeStatus);
            challengesResponseDtos.add(responseDto);

        }

        return challengesResponseDtos;
    }

    public ChallengeResponseDto getChallenge(Long challengeId) throws Exception, InternalServerException {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(() -> new NotFoundException("존재하지 않는 챌린지입니다."));
        List<String> challengeImage = new ArrayList<>();

        List<ImageFile> ImageFiles = challenge.getChallengeImage();
        if(ImageFiles.isEmpty()){
            throw new InternalServerException("챌린지 이미지를 찾을 수 없습니다.");
        }
        for (ImageFile image : ImageFiles) {
            challengeImage.add(image.getFilePath());
        }

        List<UserChallenge> userChallengeList = userChallengeRepository.findAllByChallenge(challenge);
        List<MemberResponseDto> memberList = new ArrayList<>();

         for (UserChallenge userChallenge : userChallengeList) {
             memberList.add(userChallenge.getUser().toMemberResponse());
         }
        String status = challengeStatus(challenge);

        ChallengeResponseDto challengeResponseDtos = new ChallengeResponseDto(challenge, challengeImage);
        challengeResponseDtos.setMembers(memberList);
        challengeResponseDtos.setStatus(status);
        return challengeResponseDtos;
    }


    public List<ChallengesResponseDto> getCategoryChallenge(Category categoryId) throws ParseException, InternalServerException {
        List<Challenge> challenges = challengeRepository.findAllByCategoryId(categoryId);
        List<ChallengesResponseDto> ChallengesResponseDtos = new ArrayList<>();

        for(Challenge challenge: challenges){
            List<String> challengeImages = new ArrayList<>();
            List<ImageFile> ImageFiles =  challenge.getChallengeImage();
            if(ImageFiles.isEmpty()){
                throw new InternalServerException("챌린지 이미지를 찾을 수 없습니다.");
            }
            for(ImageFile image:ImageFiles){
                challengeImages.add(image.getFilePath());
            }
            String status = challengeStatus(challenge);

            if(categoryId.equals(challenge.getCategory())){
                ChallengesResponseDto responseDto = new ChallengesResponseDto(challenge, challengeImages);
                responseDto.setStatus(status);
                ChallengesResponseDtos.add(responseDto);
            }
        }

        return ChallengesResponseDtos;
    }

    public List<ChallengesResponseDto> getKeywordChallenge(String keyword) throws ParseException, InternalServerException {
        List<Challenge> challenges = challengeRepository.findAll();
        List<ChallengesResponseDto> ChallengesResponseDtos = new ArrayList<>();

        for(Challenge c: challenges) {
            List<String> challengeImages = new ArrayList<>();
            List<ImageFile> ImageFiles = c.getChallengeImage();
            if(ImageFiles.isEmpty()){
                throw new InternalServerException("챌린지 이미지를 찾을 수 없습니다.");
            }
            for (ImageFile image : ImageFiles) {
                challengeImages.add(image.getFilePath());
            }
            String status = challengeStatus(c);
            if(c.getTitle().contains(keyword)){
                ChallengesResponseDto responseDto = new ChallengesResponseDto(c, challengeImages);
                responseDto.setStatus(status);
                ChallengesResponseDtos.add(responseDto);
            }
            for(TagChallenge t : c.getTagChallenges()){
                if(t.getTag().getName().contains(keyword)){
                    ChallengesResponseDto responseDto = new ChallengesResponseDto(c, challengeImages);
                    responseDto.setStatus(status);
                    ChallengesResponseDtos.add(responseDto);
                }

            }
        }

        return ChallengesResponseDtos;
    }

    @Transactional
    public void participateChallenge(Long challengeId, PrincipalDetails principalDetails) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NullPointerException("찾는 챌린지가 존재하지 않습니다.")
        );

        User user = principalDetails.getUser();

        if (challenge.getMaxMember() <= challenge.getCurrentMember()) {
            throw new IllegalArgumentException("인원이 가득차 참여할 수 없습니다.");
        }

        UserChallenge userChallengeCheck = userChallengeRepository.findByUserIdAndChallengeId(user.getId(), challengeId);

        if (userChallengeCheck != null) {
            throw new IllegalArgumentException("이미 참가한 챌린지입니다.");
        }

        userChallengeRepository.save(new UserChallenge(challenge, user));
        List<UserChallenge> userChallenges = userChallengeRepository.findAllByChallenge(challenge);
        challenge.setCurrentMember(userChallenges.size());

    }
    

    @Transactional
    public ResponseEntity<?> modifyChallenge(Long challengeId, ChallengeModifyRequestDto requestDto, List<MultipartFile> multipartFileList, PrincipalDetails principalDetails) throws IOException {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NullPointerException("찾는 챌린지가 존재하지 않습니다.")
        );

        if (principalDetails.getUser().getId().equals(challenge.getUser().getId())) {
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
        } else {
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }
    }

    @Transactional
    public void cancelChallenge(Long challengeId, PrincipalDetails principalDetails) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NullPointerException("찿는 챌린지가 존재하지 않습니다.")
        );
        if (challenge.getUser().getId().equals(principalDetails.getUser().getId())) {
            challengeRepository.deleteById(challengeId);
        } else {
            throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
        }
    }

    

    @Transactional
    public void privateParticipateChallenge(Long challengeId, PasswordDto passwordDto, PrincipalDetails principalDetails) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NullPointerException("찿는 챌린지가 존재하지 않습니다.")
        );

        if (challenge.getMaxMember() <= challenge.getCurrentMember()) {
            throw new IllegalArgumentException("인원이 가득차 참여할 수 없습니다.");
        }

        User user = principalDetails.getUser();

        UserChallenge userChallengeCheck = userChallengeRepository.findByUserIdAndChallengeId(user.getId(), challengeId);

        if (userChallengeCheck != null) {
            throw new IllegalArgumentException("이미 참가한 챌린지입니다.");
        }

        if (passwordEncoder.matches(passwordDto.getPassword(), challenge.getPassword())) {
            UserChallenge userChallenge = new UserChallenge(challenge, user);
            userChallengeRepository.save(userChallenge);
            List<UserChallenge> userChallengeList = userChallengeRepository.findAllByChallenge(challenge);
            challenge.setCurrentMember(userChallengeList.size());
        } else {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다");
        }
    }

}
