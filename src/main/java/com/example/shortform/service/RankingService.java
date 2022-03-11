package com.example.shortform.service;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.domain.Ranking;
import com.example.shortform.domain.User;
import com.example.shortform.domain.UserChallenge;
import com.example.shortform.dto.RequestDto.RankingRequestDto;
import com.example.shortform.dto.ResponseDto.RankingResponseDto;
import com.example.shortform.exception.NotFoundException;
import com.example.shortform.repository.RankingRepository;
import com.example.shortform.repository.UserChallengeRepository;
import com.example.shortform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.swing.table.TableCellEditor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankRepository;
    private final UserRepository userRepository;
    private final UserChallengeRepository userChallengeRepository;


    @Scheduled(cron = "0 0 0 * * *")//fixedDelay = 1000 * 60 * 60 * 24)
    public void updateRank(){
        List<User> users = userRepository.findAllByOrderByRankingPointDesc();
        Ranking rank = new Ranking(users);
        rankRepository.save(rank);


        ArrayList<Integer> rankingPointList = new ArrayList<>();

        for(User u : users){
            if(!rankingPointList.contains(u.getRankingPoint()))
                rankingPointList.add(u.getRankingPoint());
        }
        Collections.sort(rankingPointList);
        for(User u:users){
            Integer yesterdayRank = rankingPointList.indexOf(u.getRankingPoint());
            u.setYesterdayRank(yesterdayRank);
        }


         // 12시 마다 데일리 인증 초기화해주기
         List<UserChallenge> userChallenges = userChallengeRepository.findAll();
         for (UserChallenge userChallenge : userChallenges) {
             userChallenge.setDailyAuthenticated(false);
             userChallengeRepository.save(userChallenge);
      
         }
    }

    public List<RankingResponseDto> getRanking(PrincipalDetails principalDetails){

        Ranking yesterdayList = rankRepository.findTopByOrderByIdDesc();
        List<RankingResponseDto> rankDtos = new ArrayList<>();

        //======================================================

        List<User> top3Users = userRepository.findTop3ByOrderByRankingPointDesc();
        for(int i =0; i<3; i++) {

            User user = top3Users.get(i);
            RankingResponseDto rankingDto = new RankingResponseDto(user);
            List<User> users = userRepository.findAllByOrderByRankingPointDesc();

            int yesterdayRank = user.getYesterdayRank();

            ArrayList<Integer> rankingPointList = new ArrayList<>();

            for(User u : users){
                if(!rankingPointList.contains(u.getRankingPoint()))
                    rankingPointList.add(u.getRankingPoint());
            }
            Collections.sort(rankingPointList);

            int todayRank = rankingPointList.indexOf(user.getRankingPoint());

            String status = "";

            System.out.print(yesterdayRank);
            System.out.println(todayRank);
            System.out.println("==================================");

            if (!(yesterdayList.getUsers().contains(user))|| user.getYesterdayRank() == 0) {
                status = "new";
            }
            else if (yesterdayRank > todayRank) {
                status = "상승";
            } else if (yesterdayRank == todayRank) {
                status = "유지";
            } else if (yesterdayRank < todayRank) {
                status = "하강";
            }

            rankingDto.setStatus(status);
            rankingDto.setRank(todayRank);
            rankDtos.add(rankingDto);
        }

        //=====================================================

        User user = userRepository.findByEmail(principalDetails.getUser().getEmail()).orElseThrow(()->new NotFoundException("존재하지 않는 사용자입니다."));
        RankingResponseDto rankingDto = new RankingResponseDto(user);


        List<User> users = userRepository.findAllByOrderByRankingPointDesc();
        int yesterdayRank = user.getYesterdayRank();
        ArrayList<Integer> rankingPointList = new ArrayList<>();

        for(User u : users){
            if(!rankingPointList.contains(u.getRankingPoint()))
                rankingPointList.add(u.getRankingPoint());
        }
        Collections.sort(rankingPointList);

        int todayRank = rankingPointList.indexOf(user.getRankingPoint());


        String status = "";


        System.out.print(yesterdayRank);
        System.out.println(todayRank);
        System.out.println("==================================");
        if (!(yesterdayList.getUsers().contains(user))|| user.getYesterdayRank() == 0) {
            status = "new";
        } else if (yesterdayRank > todayRank) {
            status = "상승";
        } else if (yesterdayRank == todayRank) {
            status = "유지";
        } else if (yesterdayRank < todayRank) {
            status = "하강";
        }

        rankingDto.setStatus(status);
        rankingDto.setRank(todayRank);
        rankDtos.add(rankingDto);

        return rankDtos;
    }
}

