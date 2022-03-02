package com.example.shortform.service;

import com.example.shortform.domain.Tag;
import com.example.shortform.domain.User;
import com.example.shortform.dto.ResponseDto.RankingResponseDto;
import com.example.shortform.dto.ResponseDto.TagResponseDto;
import com.example.shortform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final UserRepository userRepository;
    public List<RankingResponseDto> getRanking(){

            List<User> users = userRepository.findTop5ByOrderByPoint();
            List<RankingResponseDto> rankings = new ArrayList<>();

            for (User u:users) {
                rankings.add(new RankingResponseDto(u));
            }
            return rankings;
        }
    }

