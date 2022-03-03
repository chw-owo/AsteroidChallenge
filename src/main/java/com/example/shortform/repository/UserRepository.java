package com.example.shortform.repository;

import com.example.shortform.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findTop5ByOrderByPoint();
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
    User findByProviderId(String providerId);

}