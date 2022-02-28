package com.example.shortform.service;

import com.example.shortform.domain.Category;
import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.Post;
import com.example.shortform.domain.Tag;
import com.example.shortform.dto.RequestDto.CategoryRequestDto;
import com.example.shortform.dto.RequestDto.ChallengeRequestDto;
import com.example.shortform.dto.ResponseDto.ChallengeIdResponseDto;
import com.example.shortform.dto.ResponseDto.ChallengeResponseDto;
import com.example.shortform.dto.ResponseDto.TagResponseDto;
import com.example.shortform.repository.CategoryRepository;
import com.example.shortform.repository.ChallengeRepository;
import com.example.shortform.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;
@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;

    //for test
    @Transactional
    public boolean makeCategory(CategoryRequestDto requestDto){
        Category category = new Category(requestDto);
        categoryRepository.save(category);
        return true;
    }

    public List<Challenge> getChallenge(){
        return challengeRepository.findAll();
    }


    public List<TagResponseDto> getRecommendChallenge(){

        List<Tag> allTag = tagRepository.findAll();
        Set<Tag> tagSet = new HashSet<>(allTag);
        Map<Tag, Integer> tagMap = new HashMap();

        for(Tag t :tagSet){
            Integer frequency = Collections.frequency(allTag, t);
            tagMap.put( t, frequency );
        }

        List<Map.Entry<Tag, Integer>> list_entries = new ArrayList<Map.Entry<Tag, Integer>>((Collection<? extends Map.Entry<Tag, Integer>>) tagMap.entrySet());

        // 비교함수 Comparator를 사용하여 내림차순으로 정렬
        Collections.sort(list_entries, new Comparator<Map.Entry<Tag, Integer>>() {
            @Override
            public int compare(Map.Entry<Tag, Integer> o1, Map.Entry<Tag, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        List<TagResponseDto> tagList = new ArrayList<>();

        for(Map.Entry<Tag, Integer> entry : list_entries) {
            TagResponseDto tag = TagResponseDto.builder()
                    .name(entry.getKey().getName())
                    .build();
            tagList.add(tag);
        }

        return tagList;
    }

    @Transactional
    public ChallengeResponseDto postChallenge(ChallengeRequestDto requestDto, HttpServletRequest request){

//        String token = request.getHeader(AUTHORIZATION_HEADER);
//        String userId =  tokenProvider.parseClaims(token).getSubject();
//        User user = userRepository.findByLoginId(userId).orElseThrow(()->new Exception("invalid Token"));

        Category category = categoryRepository.findByName(requestDto.getCategory());
        Challenge challenge =  new Challenge(requestDto, category);
        //challengeRepository.save(challenge);
        ChallengeResponseDto responseDto = new ChallengeResponseDto(challenge);

        return responseDto;
    }
}
