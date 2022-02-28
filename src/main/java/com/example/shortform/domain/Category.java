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
public class Category extends Timestamped{
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "category", orphanRemoval = true)
    private List<Challenge> challenges = new ArrayList<>();

    @Builder
    public Category(CategoryRequestDto requestDto) {
        this.name = requestDto.getName();
    }
}
