package com.example.shortform.repository;

import com.example.shortform.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findAllByUserId(Long id);

    boolean existsByUserIdAndNoticeLevel(Long id, Long id1);

    boolean existsByUserIdAndIsSuccessAndChallengeId(Long id, boolean b, Long challengeId);
}
