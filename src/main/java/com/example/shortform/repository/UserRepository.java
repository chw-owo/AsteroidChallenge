package com.example.shortform.repository;

import com.example.shortform.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findTop5ByOrderByPoint();
    User findByEmail(String email);
}