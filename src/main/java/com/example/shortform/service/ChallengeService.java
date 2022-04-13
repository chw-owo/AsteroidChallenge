package com.example.shortform.service;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.domain.*;
import com.example.shortform.dto.RequestDto.ChallengeRequestDto;
import com.example.shortform.dto.RequestDto.ReportRequestDto;
import com.example.shortform.dto.ResponseDto.ChallengePageResponseDto;
import com.example.shortform.dto.ResponseDto.ChallengeResponseDto;
import com.example.shortform.dto.ResponseDto.ChallengesResponseDto;
import com.example.shortform.dto.ResponseDto.ReportResponseDto;
import com.example.shortform.dto.request.ChallengeModifyRequestDto;
import com.example.shortform.dto.request.PasswordDto;
import com.example.shortform.dto.resonse.CMResponseDto;
import com.example.shortform.dto.resonse.ChallengeIdResponseDto;
import com.example.shortform.dto.resonse.MemberResponseDto;
import com.example.shortform.dto.resonse.UserChallengeInfo;
import com.example.shortform.exception.*;
import com.example.shortform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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
    private final LevelService levelService;

    private final NoticeRepository noticeRepository;

    @Transactional
    public Long postChallenge(ChallengeRequestDto requestDto,
                              PrincipalDetails principal,
                              List<MultipartFile> multipartFiles) throws IOException, ParseException {

        Category category = categoryRepository.findByName(requestDto.getCategory());
        Challenge challenge;

        if (requestDto.getPassword() != null) {
            String encPassword = passwordEncoder.encode(requestDto.getPassword());
            challenge = new Challenge(requestDto, category, encPassword,1);
        } else {
            challenge = new Challenge(requestDto, category, null,1);
        }

        List<TagChallenge> tagChallenges = new ArrayList<>();
        List<String> tagStrings = requestDto.getTagName();

        for (String tagString : tagStrings) {
            Tag tag = new Tag(tagString);
            tagRepository.save(tag);

            TagChallenge tagChallenge = new TagChallenge(challenge, tag);
            tagChallenges.add(tagChallenge);
            tagChallengeRepository.save(tagChallenge);

        }

        ArrayList<UserChallenge> userChallenges = new ArrayList<>();
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        challenge.setUser(user);
        UserChallenge userChallenge = new UserChallenge(challenge, user);
        userChallenges.add(userChallenge);
        userChallengeRepository.save(userChallenge);

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

        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        Date startDate = format.parse(requestDto.getStartDate());
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);
        startCalendar.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        LocalDate startLocalDate = LocalDateTime.ofInstant(startCalendar.toInstant(), startCalendar.getTimeZone().toZoneId()).toLocalDate();

        Date endDate = format.parse(requestDto.getEndDate());
        Calendar endCalendar = Calendar.getInstance();
        startCalendar.setTime(endDate);
        endCalendar.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);
        LocalDate endLocalDate = LocalDateTime.ofInstant(endCalendar.toInstant(), endCalendar.getTimeZone().toZoneId()).toLocalDate();

        for (LocalDate date = startLocalDate; date.isBefore(endLocalDate.plusDays(1)); date = date.plusDays(1))
        {
            AuthChallenge authChallenge = AuthChallenge.builder()
                    .challenge(challenge)
                    .date(date)
                    .currentMember(challenge.getCurrentMember())
                    .build();
            authChallengeRepository.save(authChallenge);
        }

        challengeRepository.save(challenge);

        if (user.isNewbie()) {
            Notice notice = new Notice(user, 5);
            noticeRepository.save(notice);
            user.setRankingPoint(user.getRankingPoint() + 5);
            user.setNewbie(false);
            userRepository.save(user);
        }
        LocalDate now = LocalDate.now();
        saveReport(challenge, now);

        return challenge.getId();
    }

    public void saveReport(Challenge challenge, LocalDate date){

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int dayOfWeekNumber = dayOfWeek.getValue();

        do {
            AuthChallenge authChallenge = Optional.ofNullable(authChallengeRepository.findByChallengeAndDate(challenge, date)).orElse(
                    AuthChallenge.builder()
                            .challenge(challenge)
                            .date(date)
                            .currentMember(challenge.getCurrentMember())
                            .authMember(0)
                            .build()
            );
            if(dayOfWeekNumber == 7){
                dayOfWeekNumber =1;
            }else{
                dayOfWeekNumber +=1;
            }
            date = date.plusDays(1);
            authChallengeRepository.save(authChallenge);
        }
        while(dayOfWeekNumber<7);

    }

    @Scheduled(cron = "0 0 0 * * 0")
    public void saveWeeklyReport(){

        List<Challenge> challenges = challengeRepository.findAll();
        LocalDate now = LocalDate.now();

        for(Challenge challenge : challenges){
            saveReport(challenge, now);
        }
    }

    public List<ReportResponseDto> getReport(Long challengeId, ReportRequestDto requestDto) {

        List<ReportResponseDto> responseDtos = new ArrayList<>();

        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(()-> new NotFoundException("존재하지 않는 챌린지입니다."));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate startDate = LocalDate.parse(requestDto.getStartDate(), formatter);
        LocalDate now = LocalDate.now();

        for(int i =0; i<7; i++) {
            LocalDate date = startDate.plusDays(i);
            AuthChallenge authChallenge = authChallengeRepository.findByChallengeAndDate(challenge, date);

            int division = 1;
            int divisor = 0;

            if (date.isBefore(now) || date.isEqual(now)) {
                division = authChallenge.getCurrentMember();
                divisor = authChallenge.getAuthMember();
            }

            int percentage = (int)(((double) divisor / (double) division) * 100.0);

            ReportResponseDto responseDto = ReportResponseDto.builder().date(date.toString()).percentage(percentage).build();
            responseDtos.add(responseDto);
        }

        return responseDtos;
    }

    public String challengeStatus(Challenge challenge) throws ParseException {

        LocalDate now = LocalDate.now();

        String startDate = challenge.getStartDate();
        String endDate = challenge.getEndDate();

        LocalDate localStartDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
        LocalDate localEndDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));

        if (now.isBefore(localStartDate)) {
            challenge.setStatus(ChallengeStatus.BEFORE);
            return "모집중";
        }else if (now.isBefore(localEndDate.plusDays(1))){
            challenge.setStatus(ChallengeStatus.ING);
            return "진행중";
        }else{
            challenge.setStatus(ChallengeStatus.SUCCESS);
            return "완료";
        }
    }

    public List<ChallengesResponseDto> recommendChallenges(Long challengeId, PrincipalDetails principalDetails) throws ParseException {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(()-> new NotFoundException("존재하지 않는 챌린지입니다.") );
        Category category= challenge.getCategory();
        List<Challenge> challenges = challengeRepository.findAllByCategoryIdOrderByCreatedAtDesc(category.getId());
        List<ChallengesResponseDto> challengesResponseDtos = new ArrayList<>();
        int cnt = 0;

        for(Challenge c: challenges){

            List<String> challengeImages = new ArrayList<>();
            List<ImageFile> ImageFiles =  c.getChallengeImage();

            for(ImageFile image:ImageFiles){
                challengeImages.add(image.getFilePath());
            }

            Optional<UserChallenge> userChallengeOptional = Optional.ofNullable(userChallengeRepository.findByUserIdAndChallengeId(
                    principalDetails.getUser().getId(), c.getId()));

            Optional<UserChallenge> userChallengeCheckDate = Optional.ofNullable(userChallengeRepository.findByUserIdAndChallengeId(
                    c.getUser().getId(), c.getId()));

            int challengeDate = userChallengeCheckDate.get().getChallengeDate();

            if((    !c.equals(challenge)&&
                    !userChallengeOptional.isPresent()) &&
                    (c.getMaxMember() > c.getCurrentMember()) &&
                    userChallengeCheckDate.get().getParticipateDate(challengeDate, c)
            ) {
                String challengeStatus = challengeStatus(c);
                ChallengesResponseDto responseDto = new ChallengesResponseDto(c, challengeImages);
                responseDto.setStatus(challengeStatus);
                challengesResponseDtos.add(responseDto);
                cnt++;
            }

            if (cnt >= 5){
                break;
            }
        }
        return challengesResponseDtos;
    }

    public ChallengeResponseDto getChallenge(Long challengeId) throws Exception, InternalServerException {
        Challenge challenge = challengeRepository.findChallenge(challengeId).orElseThrow(() -> new NotFoundException("존재하지 않는 챌린지입니다."));
        List<String> challengeImage = new ArrayList<>();

        List<ImageFile> ImageFiles = challenge.getChallengeImage();

        for (ImageFile image : ImageFiles) {
            challengeImage.add(image.getFilePath());
        }

        List<UserChallenge> userChallengeList = userChallengeRepository.findAllByChallenge(challenge);
        List<MemberResponseDto> memberList = new ArrayList<>();

        for (UserChallenge userChallenge : userChallengeList) {
            memberList.add(userChallenge.getUser().toMemberResponse());
        }
        String status = challengeStatus(challenge);

        ChallengeResponseDto challengeResponseDto = new ChallengeResponseDto(challenge, challengeImage);
        challengeResponseDto.setMembers(memberList);
        challengeResponseDto.setStatus(status);
        return challengeResponseDto;
    }

    public ChallengePageResponseDto getChallenges(Pageable pageable) throws ParseException {
        Page<Challenge> challengePage = challengeRepository.findAllChallenge(pageable);
        return getChallengePageResponseDto(challengePage);
    }

    public ChallengePageResponseDto getCategoryChallenge(Long categoryId, Pageable pageable) throws ParseException {
        Page<Challenge> challengePage = challengeRepository.findAllByCategoryId(categoryId, pageable);
        return getChallengePageResponseDto(challengePage);
    }

    public ChallengePageResponseDto getKeywordChallenge(String keyword, Pageable pageable) throws ParseException {
        String searchKeyword = keyword.trim();
        if (searchKeyword.equals("")) {
            return ChallengePageResponseDto.builder()
                    .challengeList(new ArrayList<>())
                    .totalCnt(0)
                    .next(false)
                    .build();
        }
        Page<Challenge> challengePage = challengeRepository.searchList(searchKeyword, pageable);
        return getChallengePageResponseDto(challengePage);
    }

    public ChallengePageResponseDto getChallengePageResponseDto(Page<Challenge> challengePage) throws ParseException {
        List<ChallengesResponseDto> challengesResponseDtoList = new ArrayList<>();

        for (Challenge challenge : challengePage) {
            List<String> challengeImages = new ArrayList<>();

            List<ImageFile> ImageFiles = challenge.getChallengeImage();

            for (ImageFile image : ImageFiles) {
                challengeImages.add(image.getFilePath());
            }
            String status = challengeStatus(challenge);
            ChallengesResponseDto responseDto = new ChallengesResponseDto(challenge, challengeImages);
            responseDto.setStatus(status);
            challengesResponseDtoList.add(responseDto);

        }
        ChallengePageResponseDto challengePageResponseDto = ChallengePageResponseDto.builder()
                .challengeList(challengesResponseDtoList)
                .next(challengePage.hasNext())
                .totalCnt(challengePage.getTotalElements())
                .build();

        return challengePageResponseDto;
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

        UserChallenge userChallenge = userChallengeRepository.findByUserIdAndChallengeId(challenge.getUser().getId(), challengeId);
        int challengeDate = userChallenge.getChallengeDate();
        if (!userChallenge.getParticipateDate(challengeDate, challenge))
            throw new InvalidException("참가 가능 날짜가 지났습니다.");

        userChallengeRepository.save(new UserChallenge(challenge, user));

        List<UserChallenge> userChallenges = userChallengeRepository.findAllByChallenge(challenge);
        challenge.setCurrentMember(userChallenges.size());
        challengeRepository.save(challenge);
        LocalDate now = LocalDate.now();
        AuthChallenge authChallenge = Optional.ofNullable(authChallengeRepository.findByChallengeAndDate(challenge, now)).orElse(
                AuthChallenge.builder()
                        .challenge(challenge)
                        .date(now)
                        .currentMember(challenge.getCurrentMember())
                        .authMember(0)
                        .build()
        );

        authChallenge.setCurrentMember(challenge.getCurrentMember());
        authChallengeRepository.save(authChallenge);

        if (user.isNewbie()) {
            Notice notice = new Notice(user, 5);
            noticeRepository.save(notice);
            user.setRankingPoint(user.getRankingPoint() + 5);
            user.setNewbie(false);
            userRepository.save(user);
        }

    }


    @Transactional
    public ResponseEntity<ChallengeIdResponseDto> modifyChallenge(Long challengeId, ChallengeModifyRequestDto requestDto, List<MultipartFile> multipartFileList, PrincipalDetails principalDetails) throws IOException {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NotFoundException("찾는 챌린지가 존재하지 않습니다.")
        );

        if (principalDetails.getUser().getId().equals(challenge.getUser().getId())) {
            if (requestDto.getImage() != null) {
                Map<String, Integer> map = new HashMap<>();
                for (int i = 0; i < requestDto.getImage().size(); i++) {
                    map.put(requestDto.getImage().get(i), i);
                }
                for (int i = 0; i < challenge.getChallengeImage().size(); i++) {
                    if (!map.containsKey(challenge.getChallengeImage().get(i).getFilePath()))
                        imageFileRepository.deleteById(challenge.getChallengeImage().get(i).getId());
                }
            }

            if (multipartFileList != null)
                imageFileService.uploadImage(multipartFileList, challenge);

            challenge.update(requestDto);

            List<TagChallenge> tagChallenges = tagChallengeRepository.findAllByChallenge(challenge);
            List<String> tagNames = requestDto.getTagName();

            Map<String, Integer> tagMap = new HashMap<>();
            for (int i = 0; i < tagNames.size(); i++) {
                tagMap.put(tagNames.get(i), i);
            }

            for (TagChallenge value : tagChallenges) {
                if (!tagMap.containsKey(value.getTag().getName()))
                    tagRepository.deleteById(value.getTag().getId());
            }

            for (String tagString : tagNames) {
                Tag tag = new Tag(tagString);
                if (!tagChallengeRepository.existsByChallengeAndTagName(challenge, tagString)) {
                    tagRepository.save(tag);
                    TagChallenge newTagChallenge = TagChallenge.builder()
                            .challenge(challenge)
                            .tag(tag)
                            .build();
                    tagChallengeRepository.save(newTagChallenge);
                }
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
        User user = userRepository.findById(principalDetails.getUser().getId()).orElseThrow(
                () -> new NotFoundException("로그인 한 유저가 아닙니다.")
        );

        if (userChallenge != null){
            userChallengeRepository.deleteByUserIdAndChallengeId(user.getId(),challengeId);
            challenge.setCurrentMember(challenge.getCurrentMember() - 1);
            challengeRepository.save(challenge);

            LocalDate now = LocalDate.now();
            String start = challenge.getStartDate().substring(0,10);
            String end = challenge.getEndDate().substring(0,10);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            LocalDate startDate = LocalDate.parse(start, formatter);
            LocalDate endDate = LocalDate.parse(end, formatter);

            if (now.isAfter(startDate.minusDays(1)) && now.isBefore(endDate.plusDays(1))) {
                user.setRankingPoint(user.getRankingPoint() - 5);
            }

            levelService.checkLevelPoint(user);

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

            List<UserChallenge> userChallenges = userChallengeRepository.findAllByChallenge(challenge);
            challenge.setCurrentMember(userChallenges.size());
            challengeRepository.save(challenge);

        } else {
            throw new InvalidException("비밀번호가 틀렸습니다");
        }

        LocalDate now = LocalDate.now();
        AuthChallenge authChallenge = Optional.ofNullable(authChallengeRepository.findByChallengeAndDate(challenge, now)).orElse(
                AuthChallenge.builder()
                        .challenge(challenge)
                        .date(now)
                        .currentMember(challenge.getCurrentMember())
                        .authMember(0)
                        .build()
        );

        authChallenge.setCurrentMember(challenge.getCurrentMember());
        authChallengeRepository.save(authChallenge);

        if (user.isNewbie()) {
            Notice notice = new Notice(user, 5);
            noticeRepository.save(notice);
            user.setRankingPoint(user.getRankingPoint() + 5);
            user.setNewbie(false);
            userRepository.save(user);
        }
    }

    @Transactional
    public ResponseEntity<CMResponseDto> deleteChallenge(Long challengeId, PrincipalDetails principalDetails) throws ParseException {

        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new NotFoundException("찿는 챌린지가 존재하지 않습니다.")
        );

        if (!principalDetails.getUser().getId().equals(challenge.getUser().getId()))
            throw new InvalidException("방장만 삭제할 수 있습니다.");

        if (!"모집중".equals(challengeStatus(challenge)))
            throw new InvalidException("모집기간일 때만 삭제할 수 있습니다.");

        if (noticeRepository.existsByChallengeIdAndUserId(challengeId, principalDetails.getUser().getId())) {
            List<Notice> noticeList = noticeRepository.findAllByChallengeIdAndUserId(challengeId, principalDetails.getUser().getId());
            for (Notice notice : noticeList) {
                notice.setNoticeType(Notice.NoticeType.RECORD);
                noticeRepository.save(notice);
            }
        }

        challengeRepository.delete(challenge);

        return ResponseEntity.ok(new CMResponseDto("true"));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<UserChallengeInfo>> getUserChallenge(Long userId) throws ParseException {
        User findUser = userRepository.findUserInfo(userId).orElseThrow(
                () -> new NotFoundException("존재하지 않는 유저입니다.")
        );

        List<UserChallenge> UserChallengeInfoList = userChallengeRepository.findAllUserChallengeInfo(findUser.getId());
        List<UserChallengeInfo> userChallengeInfoList = new ArrayList<>();

        for (UserChallenge userChallenge : UserChallengeInfoList) {

            // challenge
            Challenge challenge = userChallenge.getChallenge();

            // 진행중 챌린지 : 당일 인증하면 내가 해냄 리턴해주기
            String dailyAuth = userChallenge.isDailyAuthenticated() ? "true" : "false";

            // 완료된 챌린지 : 성공, 실패 리턴해주기
            String status = getSuccessAndFailChallenge(userChallenge, challenge);

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
            for(ImageFile image:ImageFiles)
                challengeImages.add(image.getFilePath());

            UserChallengeInfo userChallengeInfo = UserChallengeInfo.of(challenge, status, tagChallengeStrings,
                    challengeImages, dailyAuth);

            userChallengeInfoList.add(userChallengeInfo);
        }

        return ResponseEntity.ok(userChallengeInfoList);

    }

    private String getSuccessAndFailChallenge(UserChallenge userChallenge, Challenge challenge) throws ParseException {
        String status = challengeStatus(challenge);
        if ("완료".equals(status))
            status = userChallenge.isSuccessChallenge() ? "성공" : "실패"; // 인증횟수가 같거나 더 많으면 성공
        return status;
    }
}
