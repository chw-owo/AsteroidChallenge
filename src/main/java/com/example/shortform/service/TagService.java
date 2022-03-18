package com.example.shortform.service;

import com.example.shortform.domain.Tag;
import com.example.shortform.dto.ResponseDto.TagResponseDto;
import com.example.shortform.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public List<String> getRecommendChallenge(){

        List<Tag> tags = tagRepository.findAll();
        List<String> tagResponseDto = new ArrayList<>();
        Map<String, Integer> tagMap = new HashMap<>();

        for (Tag t:tags) {
            Integer count = tagMap.get(t.getName());
            if (count == null) {
                tagMap.put(t.getName(), 1);
            } else {
                tagMap.put(t.getName(), count + 1);
            }
        }

        List<String> tagKeyList = new ArrayList<>(tagMap.keySet());
        Collections.sort(tagKeyList, (value1, value2) -> (tagMap.get(value2).compareTo(tagMap.get(value1))));

        Iterator itr = tagKeyList.iterator();

        while(itr.hasNext()){
            tagResponseDto.add((String)itr.next());
        }

        return tagResponseDto;
    }
}
