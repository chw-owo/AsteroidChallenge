package com.example.shortform.repository;

import com.example.shortform.domain.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RankingRepository extends JpaRepository<Ranking, Long> {

    Ranking findTop1ByOrderByIdDesc();

    List<Ranking> findTop2ByOrderByIdDesc();

    Ranking findTopByOrderByIdDesc();
}