package com.example.shortform.domain;

import com.example.shortform.dto.ResponseDto.TagNameResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "tag", orphanRemoval = true)
    private List<TagChallenge> tagChallenges = new ArrayList<>();

    public void setName(String name) {
        this.name = name;
    }

    public TagNameResponseDto toResponse() {
        return TagNameResponseDto.builder()
                .tagName(name)
                .build();
    }
}
