package com.example.shortform.repository;

import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.DateCheck;
import com.example.shortform.domain.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DateCheckRepository extends JpaRepository<DateCheck, Long> {
}