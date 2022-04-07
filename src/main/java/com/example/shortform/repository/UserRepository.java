package com.example.shortform.repository;

import com.example.shortform.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
    User findByProviderId(String providerId);

    @Query("select u " +
            "from User u " +
            "inner join fetch u.level " +
            "where u.id = :user_id")
    Optional<User> findUserInfo(@Param("user_id") Long userId);

    List<User> findAllByOrderByRankingPointDesc();


    List<User> findAllByOrderByYesterdayRankingPointDesc();
}