package com.example.shortform.domain;

import com.example.shortform.dto.RequestDto.CategoryRequestDto;
import com.example.shortform.dto.RequestDto.ChallengeRequestDto;
import com.example.shortform.repository.CategoryRepository;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Challenge extends Timestamped{

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "challenge_image", nullable =true )
    private String challengeImage;

    @Column(name = "max_member", nullable =true)//, nullable = false)
    private int maxMember;

    @Column(name = "current_member", nullable =true)//, nullable = false)
    private int currentMember;

    @Column(name = "start_date", nullable =true)//, nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable =true)//, nullable = false)
    private LocalDate endDate;

    @Column(name = "is_private", nullable =true)//, nullable = false)
    private Boolean isPrivate = false;

    @Column(name = "password", nullable =true)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable =true)//, nullable = false)
    private ChallengeStatus status;

    @OneToMany(mappedBy = "challenge", orphanRemoval = true)
    private List<UserChallenge> userChallenges = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.MERGE)//, optional = false)
    @JoinColumn(name = "category_id", nullable =true)//false)
    private Category category;

    @OneToMany(mappedBy = "challenge", orphanRemoval = true)
    private List<TagChallenge> tagChallenges = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @ManyToOne//(optional = false)
    @JoinColumn(name = "user_id", nullable =true)//, nullable = false)
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public Challenge(ChallengeRequestDto requestDto, Category category){//List<TagChallenge> tagChallenges) {
        this.title=requestDto.getTitle();
        this.content=requestDto.getContent();
        this.category= category;
        this.challengeImage=requestDto.getChallengeImage();
        this.maxMember=requestDto.getMaxMember();
        this.startDate=requestDto.getStartDate();
        this.endDate=requestDto.getEndDate();
        this.isPrivate=requestDto.getIsPrivate();
        this.password=requestDto.getPassword();
        this.tagChallenges=null;//tagChallenges;
        //category.getChallenges().add(this);

    }
}
