package com.example.shortform.controller;

import com.example.shortform.domain.Category;
import com.example.shortform.dto.RequestDto.CategoryRequestDto;
import com.example.shortform.dto.ResponseDto.CategoryResponseDto;
import com.example.shortform.dto.ResponseDto.ChallengesResponseDto;
import com.example.shortform.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RequiredArgsConstructor
@RestController
public class CategoryController {

    private final CategoryService categoryService;
    @GetMapping("/category")
    public CategoryResponseDto getCategory(){
        return categoryService.getCategory();
    }
}
