package com.example.shortform.service;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.domain.Ranking;
import com.example.shortform.domain.User;
import com.example.shortform.dto.RequestDto.RankingRequestDto;
import com.example.shortform.dto.ResponseDto.RankingResponseDto;
import com.example.shortform.exception.NotFoundException;
import com.example.shortform.repository.RankingRepository;
import com.example.shortform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankRepository;
    private final UserRepository userRepository;

    @Scheduled(fixedDelay = 10000)//cron = "0 0 0 * * *")
    public void updateRank(){
        List<User> users = userRepository.findAllByOrderByRankingPointDesc();
        Ranking rank = new Ranking(users);
        rankRepository.save(rank);

    }

    public List<RankingResponseDto> getRanking(PrincipalDetails principalDetails){

        Ranking yesterdayList = rankRepository.findTopByOrderByIdDesc();
        List<RankingResponseDto> rankDtos = new ArrayList<>();

        //======================================================

        List<User> users = userRepository.findTop3ByOrderByRankingPointDesc();
        for(int i =0; i<3; i++) {

            User u = users.get(i);
            RankingResponseDto rankingDto = new RankingResponseDto(u);

            int yesterdayRank = yesterdayList.getUsers().indexOf(u);
            System.out.println(yesterdayRank);
            String status = "";

            if (yesterdayRank == -1) {
                status = "new";
            }
            else if (yesterdayRank > i) {
                status = "상승";
            } else if (yesterdayRank == i) {
                status = "유지";
            } else if (yesterdayRank < i) {
                status = "하강";
            }

            rankingDto.setRank(status);
            rankDtos.add(rankingDto);
        }

        //=====================================================

        User user = userRepository.findByEmail(principalDetails.getUsername()).orElseThrow(()->new NotFoundException("존재하지 않는 이메일입니다."));
        RankingResponseDto rankingDto = new RankingResponseDto(user);

        int yesterdayRank = yesterdayList.getUsers().indexOf(user);
        System.out.println(yesterdayRank);
        String status = "";

        if (yesterdayRank == -1) {
            status = "new";
        } else if (yesterdayRank > 3) {
            status = "상승";
        } else if (yesterdayRank == 3) {
            status = "유지";
        } else if (yesterdayRank < 3) {
            status = "하강";
        }

        rankingDto.setRank(status);
        rankDtos.add(rankingDto);

        return rankDtos;
    }
}

