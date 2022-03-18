package com.example.shortform.service;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.domain.*;
import com.example.shortform.dto.RequestDto.ChallengeRequestDto;
import com.example.shortform.dto.RequestDto.ReportRequestDto;
import com.example.shortform.dto.ResponseDto.ChallengeResponseDto;
import com.example.shortform.dto.ResponseDto.ChallengesResponseDto;
import com.example.shortform.dto.ResponseDto.ReportResponseDto;
import com.example.shortform.dto.request.ChallengeModifyRequestDto;
import com.example.shortform.dto.request.PasswordDto;
import com.example.shortform.dto.resonse.CMResponseDto;
import com.example.shortform.dto.resonse.MemberResponseDto;
import com.example.shortform.dto.resonse.UserChallengeInfo;
import com.example.shortform.exception.*;
import com.example.shortform.repository.*;
import jdk.jfr.Percentage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


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

    private final UserChatRoomRepository userChatRoomRepository;

    private final AuthChallengeRepository authChallengeRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional

    public Long postChallenge(ChallengeRequestDto requestDto,
                                              PrincipalDetails principal,
                                            List<MultipartFile> multipartFiles) throws IOException, ParseException {

        // 카테고리 받아오기
        Category category = categoryRepository.findByName(requestDto.getCategory());
        Challenge challenge;

        // 방 비밀번호 암호화
        if (requestDto.getPassword() != null) {
            String encPassword = passwordEncoder.encode(requestDto.getPassword());
            challenge = new Challenge(requestDto, category, encPassword);
        } else {
            challenge = new Challenge(requestDto, category, null);
        }

        // Tag, TagChallenge 저장하기
        List<TagChallenge> tagChallenges = new ArrayList<>();
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

        //User, UserChallenge 저장하기
        ArrayList<UserChallenge> userChallenges = new ArrayList<>();
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        challenge.setUser(user);
        UserChallenge userChallenge = new UserChallenge(challenge, user);
        userChallenges.add(userChallenge);
        userChallengeRepository.save(userChallenge);

        // Image, ChallengeImages 저장하기
        List<ImageFile> imageFiles = new ArrayList<>();
        List<String> challengeImages = new ArrayList<>();
        if (multipartFiles != null){
            for (MultipartFile m : multipartFiles) {
                ImageFile imageFileUpload = imageFileService.upload(m, challenge);
                imageFiles.add(imageFileUpload);
                challengeImages.add(imageFileUpload.getFilePath());
            }
        }
        challenge.ChallengeRelative(tagChallenges, userChallenges, imageFiles);


        // 위클리 레포트
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");

        Date startDate = format.parse(requestDto.getStartDate());
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);   // calendar 구조체에 오늘 날짜를 저장함
        startCalendar.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        LocalDate startLocalDate = LocalDateTime.ofInstant(startCalendar.toInstant(), startCalendar.getTimeZone().toZoneId()).toLocalDate();

        Date endDate = format.parse(requestDto.getEndDate());
        Calendar endCalendar = Calendar.getInstance();
        startCalendar.setTime(endDate);
        endCalendar.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);
        LocalDate endLocalDate = LocalDateTime.ofInstant(endCalendar.toInstant(), endCalendar.getTimeZone().toZoneId()).toLocalDate();

        for (LocalDate date = startLocalDate; date.isBefore(endLocalDate); date = date.plusDays(1))
        {
            AuthChallenge authChallenge = AuthChallenge.builder()
                    .challenge(challenge)
                    .date(date)
                    .currentMember(1)
                    .build();
            authChallengeRepository.save(authChallenge);
        }

        challengeRepository.save(challenge);

        return challenge.getId();
    }


    public List<ReportResponseDto> getReport(Long challengeId, ReportRequestDto requestDto) throws ParseException {

        List<ReportResponseDto> responseDtos = new ArrayList<>();
        List<LocalDate> dateList = new ArrayList<>();
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(()->new NotFoundException("존재하지 않는 챌린지입니다."));

        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        LocalDate now = LocalDate.now();

        Date startDate = format.parse(requestDto.getStartDate());
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);   // calendar 구조체에 오늘 날짜를 저장함
        startCalendar.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);

        for(int i =0; i<7; i++){
            startCalendar.add(Calendar.DATE, 1);
            LocalDate localDate = LocalDateTime.ofInstant(startCalendar.toInstant(), ZoneId.systemDefault()).toLocalDate();
            localDate = localDate.minusDays(1);
            dateList.add(localDate);
        }

        for (LocalDate date:dateList){

            Optional<AuthChallenge> authChallengeCheck = Optional.ofNullable(authChallengeRepository.findByChallengeAndDate(challenge, date));
            AuthChallenge authChallenge = new AuthChallenge();

            if(!authChallengeCheck.isPresent()){
                authChallenge = AuthChallenge.builder()
                        .challenge(challenge)
                        .date(date)
                        .currentMember(1)
                        .build();
                authChallengeRepository.save(authChallenge);
            }else{
                authChallenge = authChallengeRepository.findByChallengeAndDate(challenge, date);
            }

            int division = authChallenge.getCurrentMember();
            int divisor = authChallenge.getAuthMember();
            double percentage_d = 0.0;
            int percentage;

            if(authChallenge.equals(null)){
                percentage = 0;
            }
            else if(!date.isAfter(now)) {
                percentage_d = ( (double) divisor / (double) division ) * 100.0;
                percentage = (int) percentage_d;
            }else{
                percentage = 0;
            }
            ReportResponseDto responseDto = ReportResponseDto.builder().date(date.toString()).percentage(percentage).build();
            responseDtos.add(responseDto);

        }
        return responseDtos;
    }

    public String challengeStatus(Challenge challenge) throws ParseException {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date startDate = dateFormat.parse(challenge.getStartDate());
        Date endDate = dateFormat.parse(challenge.getEndDate());

        if(now.getTime() < startDate.getTime()){
            challenge.setStatus(ChallengeStatus.BEFORE);
            return "모집중";
        }else if (startDate.getTime() <= now.getTime() && now.getTime() <= endDate.getTime()){
            challenge.setStatus(ChallengeStatus.ING);
            return "진행중";
        }else{
            challenge.setStatus(ChallengeStatus.SUCCESS);
            return "완료";
        }
    }

    public List<ChallengesResponseDto> getChallenges() throws ParseException, InternalServerException {
        List<Challenge> challenges = challengeRepository.findAllByOrderByCreatedAtDesc();
        List<ChallengesResponseDto> challengesResponseDtos = new ArrayList<>();

        for(Challenge challenge: challenges){
            List<String> challengeImages = new ArrayList<>();
            List<ImageFile> ImageFiles =  challenge.getChallengeImage();

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


    public List<ChallengesResponseDto> getCategoryChallenge(Long categoryId) throws ParseException, InternalServerException {
        List<Challenge> challenges = challengeRepository.findAllByCategoryIdOrderByCreatedAtDesc(categoryId);
        List<ChallengesResponseDto> ChallengesResponseDtos = new ArrayList<>();

        for(Challenge challenge: challenges){
            List<String> challengeImages = new ArrayList<>();
            List<ImageFile> ImageFiles =  challenge.getChallengeImage();

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
        List<Challenge> challenges = challengeRepository.findAllByOrderByCreatedAtDesc();
        List<ChallengesResponseDto> ChallengesResponseDtos = new ArrayList<>();

        for(Challenge c: challenges) {
            List<String> challengeImages = new ArrayList<>();
            List<ImageFile> ImageFiles = c.getChallengeImage();
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
                () -> new NotFoundException("찾는 챌린지가 존재하지 않습니다.")
        );

        User user = principalDetails.getUser();

        if (challenge.getMaxMember() <= challenge.getCurrentMember()) {
            throw new InvalidException("인원이 가득차 참여할 수 없습니다.");
        }

        UserChallenge userChallengeCheck = userChallengeRepository.findByUserIdAndChallengeId(user.getId(), challengeId);

        if (userChallengeCheck != null) {
            throw new DuplicateException("이미 참가한 챌린지입니다.");
        }

        // 중간에 참가하는 로직 구현
        UserChallenge userChallenge = userChallengeRepository.findByUserIdAndChallengeId(challenge.getUser().getId(), challengeId);
        // 챌린지 기간
        int challengeDate = userChallenge.getChallengeDate();
        // 현재 날짜와 챌린지 참가 가능한 날짜 비교
        if (!userChallenge.getParticipateDate(challengeDate, challenge))
            throw new InvalidException("참가 가능 날짜가 지났습니다.");

        userChallengeRepository.save(new UserChallenge(challenge, user));
        List<UserChallenge> userChallenges = userChallengeRepository.findAllByChallenge(challenge);
        challenge.setCurrentMember(userChallenges.size());

        // update percentage of report - plus currentMember
        // 리포트 퍼센테이지 업데이트 - 현재 멤버 ++
        LocalDate now = LocalDate.now();
        AuthChallenge authChallenge = authChallengeRepository.findByChallengeAndDate(challenge,now);
        authChallenge.setCurrentMember(authChallenge.getCurrentMember()+1);
        authChallengeRepository.save(authChallenge);

    }


    @Transactional
    public ResponseEntity<?> modifyChallenge(Long challengeId, ChallengeModifyRequestDto requestDto, List<MultipartFile> multipartFileList, PrincipalDetails principalDetails) throws IOException {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NotFoundException("찾는 챌린지가 존재하지 않습니다.")
        );

        if (principalDetails.getUser().getId().equals(challenge.getUser().getId())) {

            // 기존 이미지가 있을 경우
            if (requestDto.getImage() != null) {

                // 해당 챌린지에 있는 이미지 중에서 받아온 기존이미지 말고는 다 삭제해주기
                for (ImageFile imageFile : challenge.getChallengeImage()) {
                    boolean isEmpty = true;
                    for (String imageUrl : requestDto.getImage()) {
                        if (imageFile.getFilePath().equals(imageUrl)) {
                            isEmpty = false;
                            break;
                        }
                    }

                    if (isEmpty)
                        imageFileRepository.deleteById(imageFile.getId()); // TODO S3에서도 삭제하기
                }
            }

            // 수정할 이미지가 있으면 challenge 에서 image 가져오기
            if (multipartFileList != null)
                imageFileService.uploadImage(multipartFileList, challenge);

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
            throw new ForbiddenException("작성자만 수정할 수 있습니다.");
        }
    }

    @Transactional
    public ResponseEntity<CMResponseDto> cancelChallenge(Long challengeId, PrincipalDetails principalDetails) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NotFoundException("찿는 챌린지가 존재하지 않습니다.")
        );

        UserChallenge userChallenge = userChallengeRepository.findByUserIdAndChallengeId(principalDetails.getUser().getId(),challengeId);
        User user = principalDetails.getUser();

        if (userChallenge != null){
            userChallengeRepository.deleteByUserIdAndChallengeId(user.getId(),challengeId);
            user.setRankingPoint(user.getRankingPoint() - 50);
            challenge.setCurrentMember(challenge.getCurrentMember() - 1);

            UserChatRoom userChatRoom = userChatRoomRepository.findByChatRoomAndUser(challenge.getChatRoom(), user);

            if (userChatRoom != null) {
                userChatRoomRepository.deleteByChatRoomIdAndUserId(challenge.getChatRoom().getId(), user.getId());
            }

        } else {
            throw new ForbiddenException("참가하지 않은 챌린지입니다.");
        }

        LocalDate now = LocalDate.now();
        AuthChallenge authChallenge = authChallengeRepository.findByChallengeAndDate(challenge,now);
        authChallenge.setCurrentMember(authChallenge.getCurrentMember()-1);
        if (userChallenge.isDailyAuthenticated()){
            authChallenge.setAuthMember(authChallenge.getAuthMember()-1);
        }
        authChallengeRepository.save(authChallenge);

        return ResponseEntity.ok(new CMResponseDto("true"));
    }



    @Transactional
    public void privateParticipateChallenge(Long challengeId, PasswordDto passwordDto, PrincipalDetails principalDetails) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NotFoundException("찿는 챌린지가 존재하지 않습니다.")
        );

        if (!passwordDto.getPassword().matches("^[0-9]{4}$")) {
            throw new InvalidException("비밀번호는 숫자 4자리 형식입니다.");
        }

        if (challenge.getMaxMember() <= challenge.getCurrentMember()) {
            throw new InvalidException("인원이 가득차 참여할 수 없습니다.");
        }

        User user = principalDetails.getUser();

        UserChallenge userChallengeCheck = userChallengeRepository.findByUserIdAndChallengeId(user.getId(), challengeId);

        if (userChallengeCheck != null) {
            throw new DuplicateException("이미 참가한 챌린지입니다.");
        }

        if (passwordEncoder.matches(passwordDto.getPassword(), challenge.getPassword())) {
            UserChallenge userChallenge = new UserChallenge(challenge, user);
            userChallengeRepository.save(userChallenge);
            List<UserChallenge> userChallengeList = userChallengeRepository.findAllByChallenge(challenge);
            challenge.setCurrentMember(userChallengeList.size());
        } else {
            throw new InvalidException("비밀번호가 틀렸습니다");
        }

        // update percentage of report - plus currentMember
        // 리포트 퍼센테이지 업데이트 - 현재 멤버 ++
        LocalDate now = LocalDate.now();
        AuthChallenge authChallenge = authChallengeRepository.findByChallengeAndDate(challenge,now);
        authChallenge.setCurrentMember(authChallenge.getCurrentMember()+1);
        authChallengeRepository.save(authChallenge);
    }

    public ResponseEntity<CMResponseDto> deleteChallenge(Long challengeId, PrincipalDetails principalDetails) throws ParseException {

        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NotFoundException("찿는 챌린지가 존재하지 않습니다.")
        );

        // 방장 아닐 경우
        if (!principalDetails.getUser().getId().equals(challenge.getUser().getId()))
            throw new InvalidException("방장만 삭제할 수 있습니다.");

        // 모집중이 아닐 경우
        if (!"모집중".equals(challengeStatus(challenge)))
            throw new InvalidException("모집기간일 때만 삭제할 수 있습니다.");

        // 해당 챌린지 삭제
        challengeRepository.delete(challenge);

        return ResponseEntity.ok(new CMResponseDto("true"));
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<List<UserChallengeInfo>> getUserChallenge(Long userId) throws ParseException {
        User findUser = userRepository.findUserInfo(userId).orElseThrow(
                () -> new NotFoundException("존재하지 않는 유저입니다.")
        );

        List<UserChallenge> UserChallengeInfoList = userChallengeRepository.findAllUserChallengeInfo(findUser.getId());
        List<UserChallengeInfo> userChallengeInfoList = new ArrayList<>();

        for (UserChallenge userChallenge : UserChallengeInfoList) {

            // challenge
            Challenge challenge = userChallenge.getChallenge();
            // status
            String status = challengeStatus(challenge);

            String dailyAuth = null;
            // 진행중 챌린지 : 포스트 인증하면 내가 해냄 리턴해주기
            if (userChallenge.isDailyAuthenticated())
                dailyAuth = "true";
            else
                dailyAuth = "false";

            // 완료된 챌린지에서 성공, 실패 리턴해주기
            if ("완료".equals(status)) {
                // 성공일수(챌린지 진행일 * 0.8) > 인증횟수
                if ((int)Math.ceil(userChallenge.getChallengeDate() * 0.8) > userChallenge.getAuthCount())
                    status = "실패";
                    // 인증횟수가 같거나 더 많으면 성공
                else
                    status = "성공";
            }

            // tag
            List<String> tagChallengeStrings = new ArrayList<>();
            List<TagChallenge> tagChallenges = challenge.getTagChallenges();
            for(TagChallenge tagChallenge : tagChallenges){
                String tagChallengeString = tagChallenge.getTag().getName();
                tagChallengeStrings.add(tagChallengeString);
            }

            // image
            List<String> challengeImages = new ArrayList<>();
            List<ImageFile> ImageFiles =  challenge.getChallengeImage();
            for(ImageFile image:ImageFiles){
                challengeImages.add(image.getFilePath());
            }

            UserChallengeInfo userChallengeInfo = UserChallengeInfo.of(challenge, status, tagChallengeStrings,
                    challengeImages, dailyAuth);

            userChallengeInfoList.add(userChallengeInfo);
        }

        return ResponseEntity.ok(userChallengeInfoList);

    }
}
