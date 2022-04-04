package com.example.shortform.domain;

import com.example.shortform.dto.RequestDto.CategoryRequestDto;
import com.example.shortform.dto.RequestDto.ChallengeRequestDto;
import com.example.shortform.repository.CategoryRepository;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.shortform.dto.request.ChallengeModifyRequestDto;
import com.example.shortform.dto.resonse.ChallengeIdResponseDto;
import com.example.shortform.dto.resonse.ChallengeResponseDto;
import com.example.shortform.dto.resonse.MemberResponseDto;
import com.example.shortform.dto.resonse.TagNameResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "max_member", nullable = false)
    private int maxMember;

    @Setter
    @Column(name = "current_member", nullable = false)
    private int currentMember;

    @Column(name = "start_date", nullable = false)
    private String startDate; //LocalDate

    @Column(name = "end_date", nullable = false)
    private String endDate; //LocalDate

    @Column(name = "is_private", nullable = false)
    private Boolean isPrivate = false;

    @Column(name = "password")
    private String password;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ChallengeStatus status;

    @OneToMany(mappedBy = "challenge", orphanRemoval = true)
    private List<UserChallenge> userChallenges = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", orphanRemoval = true)
    private List<AuthChallenge> authChallenges = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, optional = false)
    @JoinColumn(name = "category_id", nullable =false)
    private Category category;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.REMOVE)
    private List<TagChallenge> tagChallenges = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", orphanRemoval = true)
    private List<ImageFile> challengeImage = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", orphanRemoval = true)
    private List<UserChallenge> memberList = new ArrayList<>();


    public Challenge(ChallengeRequestDto requestDto, Category category, String password, int currentMember){
        this.title=requestDto.getTitle();
        this.content=requestDto.getContent();
        this.category= category;
        this.maxMember=requestDto.getMaxMember();
        this.startDate=requestDto.getStartDate();
        this.endDate=requestDto.getEndDate();
        this.isPrivate=requestDto.getIsPrivate();
        this.password = password;
        this.currentMember = currentMember;
    }

    public void ChallengeRelative(List<TagChallenge> tags, List<UserChallenge> users, List<ImageFile> images){
        this.tagChallenges = tags;
        this.userChallenges = users;
        this.challengeImage = images;
    }

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @Setter
    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;



    public void update(ChallengeModifyRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.category.builder().name(requestDto.getCategory()).build();
    }

    public ChallengeIdResponseDto toResponse() {
        return ChallengeIdResponseDto.builder()
                .challengeId(this.id)
                .build();
    }



}
