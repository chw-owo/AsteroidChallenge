package com.example.shortform.service;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.domain.User;
import com.example.shortform.domain.UserChallenge;
import com.example.shortform.dto.ResponseDto.RankingResponseDto;
import com.example.shortform.exception.NotFoundException;
import com.example.shortform.repository.RankingRepository;
import com.example.shortform.repository.UserChallengeRepository;
import com.example.shortform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final UserRepository userRepository;
    private final UserChallengeRepository userChallengeRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void updateRanks() {

        List<User> users = userRepository.findAllByOrderByRankingPointDesc();

        ArrayList<Integer> rankingPointList = new ArrayList<>();
        for (User user : users) {
            if (!rankingPointList.contains(user.getRankingPoint()))
                rankingPointList.add(user.getRankingPoint());
        }
        Collections.sort(rankingPointList, Comparator.reverseOrder());

        for (User user : users) {

            int yesterdayRank = user.getYesterdayRank();
            int todayRank = rankingPointList.indexOf(user.getRankingPoint()) + 1;
            String status = "";

            if (yesterdayRank == -1) {
                status = "유지";
            } else if (yesterdayRank > todayRank) {
                status = "상승";
            } else if (yesterdayRank == todayRank) {
                status = "유지";
            } else {
                status = "하강";
            }

            user.setRanking(status,todayRank,user.getRankingPoint());
            userRepository.save(user);
        }

        List<UserChallenge> userChallenges = userChallengeRepository.findAll();
        for (UserChallenge userChallenge : userChallenges) {
            userChallenge.setDailyAuthenticated(false);
            userChallengeRepository.save(userChallenge);

        }

    }

    @Transactional
    public void updateRank(User user) {
        List<User> users = userRepository.findAllByOrderByRankingPointDesc();

        ArrayList<Integer> rankingPointList = new ArrayList<>();
        for (User u : users) {
            if (!rankingPointList.contains(u.getRankingPoint()))
                rankingPointList.add(u.getRankingPoint());
        }
        Collections.sort(rankingPointList, Comparator.reverseOrder());

        int yesterdayRank = user.getYesterdayRank();

        int todayRank = rankingPointList.indexOf(user.getRankingPoint()) + 1;
        String status = "";

        if (yesterdayRank == -1) {
            status = "유지";
        } else if (yesterdayRank > todayRank) {
            status = "상승";
        } else if (yesterdayRank == todayRank) {
            status = "유지";
        } else {
            status = "하강";
        }

        user.setRanking(status,todayRank,user.getRankingPoint());
        userRepository.save(user);

    }

    public List<RankingResponseDto> getRanking(PrincipalDetails principalDetails){

        List<RankingResponseDto> rankDtos = new ArrayList<>();
        List<User> top3Users = userRepository.findAllByOrderByYesterdayRankingPointDesc();

        for(int i =0; i<3; i++) {
            User user = top3Users.get(i);
            RankingResponseDto rankingDto = new RankingResponseDto(user);
            rankDtos.add(rankingDto);
        }

        User user = userRepository.findByEmail(principalDetails.getUser().getEmail())
                .orElseThrow(()->new NotFoundException("존재하지 않는 사용자입니다."));
        RankingResponseDto rankingDto = new RankingResponseDto(user);
        rankDtos.add(rankingDto);

        return rankDtos;
    }
}

