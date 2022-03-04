package com.example.shortform;

import com.example.shortform.domain.Category;
import com.example.shortform.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationRunnerImpl implements ApplicationRunner{


    @Autowired
    private CategoryRepository categoryRepository;


    @Bean
    public ApplicationRunner applicationRunner() {
        String[] categoryList = new String[]{"일상 루틴", "운동", "스터디", "식습관","힐링", "취미", "셀프케어", "펫", "친환경" };
        return args -> {
            for(String c: categoryList) {
                Category category = new Category(c);
                categoryRepository.save(category);
            }
        };
    }



    @Override
    public void run(ApplicationArguments args) throws Exception {

    }
}
