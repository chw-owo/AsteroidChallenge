package com.example.shortform.service;

import com.example.shortform.domain.Level;
import com.example.shortform.domain.User;
import com.example.shortform.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LevelService {

    private final LevelRepository levelRepository;

    @Transactional
    public boolean checkLevelPoint(User user) {

        // 사용자 경험치 가져오기
        int userPoint = user.getRankingPoint();

        // 기존 유저의 레벨
        String userLevel = user.getLevel().getName();

        // 포인트가 해당 레벨의 경험치와 같으면 유저의 레벨 변경해주기
        List<Level> levelList = levelRepository.findAll();

        for (Level level : levelList) {
            if (userPoint >= level.getExperiencePoint())
                user.changeLevel(level);
        }

        if (userPoint <= 50)
            user.changeLevel(levelRepository.findById(1L).get());

        // 경험치 확인 후 유저 레벨
        String newUserLevel = user.getLevel().getName();

        return checkLevelUp(userLevel, newUserLevel);
    }

    public boolean checkLevelUp(String userLevel, String newUserLevel) {
        return !userLevel.equals(newUserLevel);
    }
}
