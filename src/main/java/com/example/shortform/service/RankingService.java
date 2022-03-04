package com.example.shortform.service;

import com.example.shortform.domain.User;
import com.example.shortform.dto.RequestDto.RankingRequestDto;
import com.example.shortform.dto.ResponseDto.RankingResponseDto;
import com.example.shortform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final UserRepository userRepository;

    public int setRanking(RankingRequestDto requestDto) {
        User user = userRepository.findByNickname(requestDto.getNickname()).orElseThrow(()->new IllegalArgumentException());
        int point = requestDto.getRankingPoint();
        user.setRankingPoint(point);
        userRepository.save(user);
        return user.getRankingPoint();

    }
    public List<RankingResponseDto> getRanking(){

            List<User> users = userRepository.findTop3ByOrderByRankingPointDesc();
            List<RankingResponseDto> rankings = new ArrayList<>();

            for (User u:users) {
                rankings.add(new RankingResponseDto(u));
            }
            //rankings.add(new RankingResponseDto(로그인된user));
            return rankings;
        }
    }

