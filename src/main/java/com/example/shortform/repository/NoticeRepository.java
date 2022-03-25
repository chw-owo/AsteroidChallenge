package com.example.shortform.repository;

import com.example.shortform.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findAllByUserIdOrderByCreatedAtDesc(Long id);

    boolean existsByUserIdAndNoticeLevel(Long id, Long id1);

    boolean existsByUserIdAndIsSuccessAndChallengeId(Long id, boolean b, Long challengeId);

    boolean existsByChallengeId(Long challengeId);

    Notice findByChallengeId(Long challengeId);

    boolean existsByRoomId(Long id);

    Notice findByRoomId(Long id);

    boolean existsByUserIdAndNoticeTypeAndCreatedAtBetween(Long id, Notice.NoticeType recommend, LocalDateTime today, LocalDateTime plusDays);
}
