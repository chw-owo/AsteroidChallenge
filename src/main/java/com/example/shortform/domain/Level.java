package com.example.shortform.domain;

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
public class Level extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")//, nullable = false)
    private String name;

    @Column(name = "level_icon")//, nullable = false)
    private String levelIcon;

    @Column(name = "experience_point")//, nullable = false)
    private int experiencePoint;

    @Column(name = "next_point")//, nullable = false)
    private int nextPoint;

    @OneToMany(mappedBy = "level", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();

}
