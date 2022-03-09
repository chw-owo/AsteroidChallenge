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

import javax.swing.table.TableCellEditor;
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

        List<User> top3Users = userRepository.findTop3ByOrderByRankingPointDesc();
        for(int i =0; i<3; i++) {

            User u = top3Users.get(i);
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

        User user = userRepository.findByEmail(principalDetails.getUser().getEmail()).orElseThrow(()->new NotFoundException("존재하지 않는 사용자입니다."));
        RankingResponseDto rankingDto = new RankingResponseDto(user);

        List<User> users = userRepository.findAllByOrderByRankingPointDesc();
        int todayRank = 1;

        for(User u:users){
            if(user.getRankingPoint() == u.getRankingPoint()){
                break;
            }
            todayRank++;
        }

        int yesterdayRank = yesterdayList.getUsers().indexOf(user);
        String status = "";

        System.out.print(todayRank);
        System.out.println(yesterdayRank);

        if (yesterdayRank == -1) {
            status = "new";
        } else if (yesterdayRank > todayRank) {
            status = "상승";
        } else if (yesterdayRank == todayRank) {
            status = "유지";
        } else if (yesterdayRank < todayRank) {
            status = "하강";
        }

        rankingDto.setRank(status);
        rankDtos.add(rankingDto);

        return rankDtos;
    }
}

