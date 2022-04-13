package com.example.shortform.controller;

import com.example.shortform.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RequiredArgsConstructor
@RestController
public class TagController {

    private final TagService tagService;

    @GetMapping("/challenge/recommend")
    public List<String> getRecommendChallenge(){
        return tagService.getRecommendChallenge();
    }
}
