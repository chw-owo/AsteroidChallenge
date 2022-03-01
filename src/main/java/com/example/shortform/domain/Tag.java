package com.example.shortform.domain;

import com.example.shortform.dto.RequestDto.CategoryRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Tag extends Timestamped{
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "tag", orphanRemoval = true, targetEntity = TagChallenge.class)
    private List<TagChallenge> tagChallenges = new ArrayList<>();

    @Builder
    public Tag(String name) {
        this.name = name;

    }
}
