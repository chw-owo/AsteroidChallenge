package com.example.shortform.service;

import com.example.shortform.domain.Level;
import com.example.shortform.domain.Notice;
import com.example.shortform.domain.User;
import com.example.shortform.repository.LevelRepository;
import com.example.shortform.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LevelService {

    private final LevelRepository levelRepository;
    private final NoticeRepository noticeRepository;

    @Transactional
    public boolean checkLevelPoint(User user) {

        // 사용자 경험치 가져오기
        int userPoint = user.getRankingPoint();

        // 기존 유저의 레벨
        String userLevel = user.getLevel().getName();

        // 포인트가 해당 레벨의 경험치와 같으면 유저의 레벨 변경해주기
        List<Level> levelList = levelRepository.findAll();

        // 기존 레벨 경험치보다 유저 레벨 경험치가 많을 경우만 레벨 비교해주기
        if (user.getLevel().getExperiencePoint() < userPoint) {
            for (Level level : levelList) {
                if (userPoint >= level.getExperiencePoint()) // 15 <= 15
                    user.changeLevel(level);
            }
        }

        // 경험치 확인 후 유저 레벨
        String newUserLevel = user.getLevel().getName();

        // 레벨업 근처일 시 알림 (중복알림 제거 필요)
        if (!checkLevelUp(userLevel, newUserLevel)) {
            Level userPresentLevel = levelRepository.findByName(userLevel);
            if (!noticeRepository.existsByUserIdAndNoticeLevel(user.getId(), userPresentLevel.getId())){
                if (userPresentLevel.getNextPoint() - userPoint <= 5 && userPresentLevel.getNextPoint() - userPoint > 0) {
                    Notice notice = Notice.builder()
                            .noticeType(Notice.NoticeType.LEVEL)
                            .is_read(false)
                            .user(user)
                            .noticeLevel(userPresentLevel.getId())
                            .build();

                    noticeRepository.save(notice);
                }
            }
        }

        return checkLevelUp(userLevel, newUserLevel);
    }

    public boolean checkLevelUp(String userLevel, String newUserLevel) {
        return !userLevel.equals(newUserLevel);
    }
}
