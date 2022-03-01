package com.example.shortform.service;

import com.example.shortform.domain.Category;
import com.example.shortform.domain.Challenge;
import com.example.shortform.dto.RequestDto.CategoryRequestDto;
import com.example.shortform.dto.ResponseDto.CategoryResponseDto;
import com.example.shortform.dto.ResponseDto.ChallengeResponseDto;
import com.example.shortform.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

//    for test
//    @Transactional
//    public boolean makeCategory(CategoryRequestDto requestDto){
//        Category category = new Category(requestDto);
//        categoryRepository.save(category);
//        return true;
//    }

    public CategoryResponseDto getCategory(){
        List<Category> categories = categoryRepository.findAll();
        List<Long> categoryId = new ArrayList<>();

        for(Category category: categories){
            categoryId.add(category.getId());

        }

        CategoryResponseDto categoryResponseDtos = new CategoryResponseDto(categoryId);

        return categoryResponseDtos;
    }
}
