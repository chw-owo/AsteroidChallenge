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

        int userPoint = user.getRankingPoint();
        String userLevel = user.getLevel().getName();
        List<Level> levelList = levelRepository.findAll();

        if (user.getLevel().getExperiencePoint() < userPoint) {
            for (Level level : levelList) {
                if (userPoint >= level.getExperiencePoint())
                    user.changeLevel(level);
            }
        }

        String newUserLevel = user.getLevel().getName();
        if (!checkLevelUp(userLevel, newUserLevel)) {
            Level userPresentLevel = levelRepository.findByName(userLevel);
            if (!noticeRepository.existsByUserIdAndNoticeLevel(user.getId(), userPresentLevel.getId())){
                if (userPresentLevel.getNextPoint() - userPoint <= 5 && userPresentLevel.getNextPoint() - userPoint > 0) {
                    Notice notice = new Notice(user, userPresentLevel);
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
