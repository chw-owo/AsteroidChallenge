package com.example.shortform.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Ranking extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToMany(targetEntity = User.class)
    @JoinColumn(name="users")
    private List<User> users;

    public Ranking(List<User> users) {
        this.users = users;
    }

}