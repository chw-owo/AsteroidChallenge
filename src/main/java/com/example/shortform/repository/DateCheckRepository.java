package com.example.shortform.repository;

import com.example.shortform.domain.DateCheck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DateCheckRepository extends JpaRepository<DateCheck, Long> {
}