package com.example.shortform.repository;

import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.User;
import com.example.shortform.domain.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {
    List<UserChallenge> findAllByChallenge(Challenge challenge);
    List<UserChallenge> findAllByUser(User user);
    UserChallenge findByUserIdAndChallengeId(Long id, Long challengeId);

    void deleteByUserIdAndChallengeId(Long id, Long challengeId);

    @Query("select uc from UserChallenge uc " +
            "inner join fetch uc.challenge " +
            "inner join fetch uc.user " +
            "where uc.user.id = :user_id " +
            "order by uc.challenge.createdAt desc ")
    List<UserChallenge> findAllUserChallengeInfo(@Param("user_id") Long user_id);

}