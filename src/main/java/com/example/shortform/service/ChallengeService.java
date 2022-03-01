package com.example.shortform.service;

import com.example.shortform.domain.*;
import com.example.shortform.dto.RequestDto.CategoryRequestDto;
import com.example.shortform.dto.RequestDto.ChallengeRequestDto;
import com.example.shortform.dto.ResponseDto.ChallengeIdResponseDto;
import com.example.shortform.dto.ResponseDto.ChallengeResponseDto;
import com.example.shortform.dto.ResponseDto.TagResponseDto;
import com.example.shortform.repository.CategoryRepository;
import com.example.shortform.repository.ChallengeRepository;
import com.example.shortform.repository.TagChallengeRepository;
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
    private final TagChallengeRepository tagChallengeRepository;

    //for test
    @Transactional
    public boolean makeCategory(CategoryRequestDto requestDto){
        Category category = new Category(requestDto);
        categoryRepository.save(category);
        return true;
    }

    @Transactional
    public ChallengeResponseDto postChallenge(ChallengeRequestDto requestDto){

        Category category = categoryRepository.findByName(requestDto.getCategory());
        List<Tag> tags = new ArrayList<>();
        List<TagChallenge> tagChallenges = new ArrayList<>();

        Challenge challenge =  new Challenge(requestDto, category);
        List<String> tagStrings = requestDto.getTagName();
        for(String tagString:tagStrings){
            Tag tag = new Tag(tagString);
            tagRepository.save(tag);

            TagChallenge tagChallenge = new TagChallenge(challenge, tag);
            tagChallenges.add(tagChallenge);
            tagChallengeRepository.save(tagChallenge);
        }

        challenge.setTagChallenges(tagChallenges);
        challengeRepository.save(challenge);
        ChallengeResponseDto responseDto = new ChallengeResponseDto(challenge);

        return responseDto;
    }

    public List<ChallengeResponseDto> getChallenge(){
        List<Challenge> challenges = challengeRepository.findAll();
        List<ChallengeResponseDto> challengeResponseDtos = new ArrayList<>();

        for(Challenge challenge: challenges){
            ChallengeResponseDto responseDto = new ChallengeResponseDto(challenge);
            challengeResponseDtos.add(responseDto);
        }

        return challengeResponseDtos;
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


}
