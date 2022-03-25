package com.example.shortform.scheduler;

import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.Notice;
import com.example.shortform.domain.User;
import com.example.shortform.domain.UserChallenge;
import com.example.shortform.repository.NoticeRepository;
import com.example.shortform.repository.UserChallengeRepository;
import com.example.shortform.repository.UserRepository;
import com.example.shortform.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Scheduler {

    private final NoticeRepository noticeRepository;
    private final ChallengeService challengeService;
    private final UserRepository userRepository;
    private final UserChallengeRepository userChallengeRepository;

    // 12시에 보내는 인증해야 하는 수량 알림
    @Transactional
    @Scheduled(cron = "30 * * * * *")
    public void morningScheduler() throws ParseException{
        List<User> totalUserList = userRepository.findAll();

        for (User user : totalUserList) {
            List<Challenge> challengingList = new ArrayList<>();
            List<UserChallenge> userChallengeList = userChallengeRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId());
            for (UserChallenge userChallenge : userChallengeList) {
                String status = challengeService.challengeStatus(userChallenge.getChallenge());
                if (status.equals("진행중")) {
                    challengingList.add(userChallenge.getChallenge());
                }
            }
            if (challengingList.size() != 0) {
                Notice notice = Notice.builder()
                        .noticeType(Notice.NoticeType.MORNING_CALL)
                        .is_read(false)
                        .user(user)
                        .challengeCnt(challengingList.size())
                        .challenge(challengingList.get(0))
                        .build();
                noticeRepository.save(notice);
            }
        }

    }

    // 마지막날 인증글 안쓴 인원 추천 알림
//    @Scheduled(cron = "0 1 0 * * *")
//    public void recommendScheduler() throws ParseException{
//        List<User> totalUserList = userRepository.findAll();
//
//        for (User user : totalUserList) {
//            List<Challenge> challengingList = new ArrayList<>();
//            List<UserChallenge> userChallengeList = userChallengeRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId());
//            for (UserChallenge userChallenge : userChallengeList) {
//                String status = challengeService.challengeStatus(userChallenge.getChallenge());
//                if (status.equals("완료")) {
//                    challengingList.add(userChallenge.getChallenge());
//                }
//            }
//            if (challengingList.size() != 0) {
//                Notice notice = Notice.builder()
//                        .noticeType(Notice.NoticeType.MORNING_CALL)
//                        .is_read(false)
//                        .user(user)
//                        .challengeCnt(challengingList.size())
//                        .challenge(challengingList.get(0))
//                        .build();
//                noticeRepository.save(notice);
//            }
//        }
//
//    }

    // 모집중 -> 진행중 챌린지 알림
    @Scheduled(cron = "10 * * * * *")
    @Transactional
    public void startSchedule() throws ParseException {
        LocalDate now = LocalDate.now();
        LocalDateTime today = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0, 0);

        List<User> totalUserList = userRepository.findAll();

        for (User user : totalUserList) {
            List<UserChallenge> userChallengeList = userChallengeRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId());
            for (UserChallenge userChallenge : userChallengeList) {
                String status = challengeService.challengeStatus(userChallenge.getChallenge());
                if (status.equals("진행중")) {
                    if (userChallenge.getChallenge().getStartDate().equals(today.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")))) {
                        Notice notice = Notice.builder()
                                .noticeType(Notice.NoticeType.INITIAL)
                                .is_read(false)
                                .user(user)
                                .challenge(userChallenge.getChallenge())
                                .build();

                        noticeRepository.save(notice);
                    }
                }
            }

        }
    }

}
