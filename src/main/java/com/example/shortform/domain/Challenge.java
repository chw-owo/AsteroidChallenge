package com.example.shortform.domain;

import com.example.shortform.dto.RequestDto.CategoryRequestDto;
import com.example.shortform.dto.RequestDto.ChallengeRequestDto;
import com.example.shortform.repository.CategoryRepository;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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

    @OneToOne(mappedBy = "challenge", orphanRemoval = true)
    private ImageFile challengeImage;

    @Column(name = "max_member", nullable = false)
    private int maxMember;

    @Column(name = "current_member", nullable = false)
    private int currentMember;

    //수정해야됨
    @Column(name = "start_date", nullable = false)
    private String startDate; //LocalDate

    //수정해야됨
    @Column(name = "end_date", nullable = false)
    private String endDate; //LocalDate

    @Column(name = "is_private", nullable = false)
    private Boolean isPrivate = false;

    @Column(name = "password", nullable =false)
    private String password;

    //수정해야됨
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable =  false)
    private String status;

    @ManyToOne(cascade = CascadeType.MERGE, optional = false)
    @JoinColumn(name = "category_id", nullable =false)
    private Category category;

    @OneToMany(mappedBy = "challenge", orphanRemoval = true)
    private List<TagChallenge> tagChallenges = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    // 수정해야됨
    @ManyToOne//(optional = false)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    //수정해야됨
    @OneToMany(mappedBy = "challenge", orphanRemoval = true)
    @JoinColumn(name = "challenge", nullable = true)
    private List<UserChallenge> userChallenges = new ArrayList<>();


    public Challenge(ChallengeRequestDto requestDto, Category category){
        this.title=requestDto.getTitle();
        this.content=requestDto.getContent();
        this.category= category;
        this.challengeImage= (ImageFile) requestDto.getChallengeImage();
        this.maxMember=requestDto.getMaxMember();
        this.startDate=requestDto.getStartDate();
        this.endDate=requestDto.getEndDate();
        this.isPrivate=requestDto.getIsPrivate();
        this.password=requestDto.getPassword();
    }
}
