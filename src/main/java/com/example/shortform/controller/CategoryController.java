package com.example.shortform.controller;

import com.example.shortform.dto.resonse.CategoryResponseDto;
import com.example.shortform.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CategoryController {

    private final CategoryService categoryService;
    @GetMapping("/category")
    public CategoryResponseDto getCategory(){
        return categoryService.getCategory();
    }
}
