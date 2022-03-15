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
@Setter
@Entity
public class Challenge extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

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

    @Column(name = "password")
    private String password;

    //수정해야됨
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ChallengeStatus status;

    @OneToMany(mappedBy = "challenge", orphanRemoval = true)
    private List<UserChallenge> userChallenges = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", orphanRemoval = true)
    private List<AuthChallenge> authChallenges = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.MERGE, optional = false)
    @JoinColumn(name = "category_id", nullable =false)
    private Category category;

    @OneToMany(mappedBy = "challenge", orphanRemoval = true)
    private List<TagChallenge> tagChallenges = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", orphanRemoval = true)
    private List<ImageFile> challengeImage = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", orphanRemoval = true)
    private List<UserChallenge> memberList = new ArrayList<>();

    @OneToOne(mappedBy = "challenge", orphanRemoval = true)
    private ChatRoom chatRoom;


    public Challenge(ChallengeRequestDto requestDto, Category category){
        this.title=requestDto.getTitle();
        this.content=requestDto.getContent();
        this.category= category;
        //this.challengeImage= challengeImage;
        this.maxMember=requestDto.getMaxMember();
        this.startDate=requestDto.getStartDate();
        this.endDate=requestDto.getEndDate();
        this.isPrivate=requestDto.getIsPrivate();
        this.password=requestDto.getPassword();
        this.currentMember = 1; // 참가인원 방장 포함
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


//    public ChallengeResponseDto toResponse(List<TagNameResponseDto> tagNameList,
//                                           List<MemberResponseDto> memberList,
//                                           List<String> imagePathList) {
//        return ChallengeResponseDto.builder()
//                .challengeId(id)
////                .userId(user.getId())
//                .title(title)
//                .content(content)
//                .categoryName(category.getName())
//                .maxMember(maxMember)
//                .currentMember(currentMember)
//                .startDate(startDate)
//                .endDate(endDate)
//                .isPrivate(isPrivate)
//                .tagNameList(tagNameList)
//                .members(memberList)
//                .imageUrlList(imagePathList)
//                .build();
//    }
//
//    public ChallengeResponseDto toSearchResponse(List<TagNameResponseDto> tagNameList,
//                                           List<MemberResponseDto> memberList
//                                           ) {
//        return ChallengeResponseDto.builder()
//                .challengeId(id)
////                .userId(user.getId())
//                .title(title)
//                .content(content)
//                .categoryName(category.getName())
//                .maxMember(maxMember)
//                .currentMember(currentMember)
//                .startDate(startDate)
//                .endDate(endDate)
//                .isPrivate(isPrivate)
//                .tagNameList(tagNameList)
//                .members(memberList)
//                .build();
//    }

    public void update(ChallengeModifyRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.category.setName(requestDto.getCategory());
    }

    public ChallengeIdResponseDto toResponse() {
        return ChallengeIdResponseDto.builder()
                .challengeId(this.id)
                .build();
    }

    public void setImageFiles(List<ImageFile> imageFileList) {
        this.challengeImage = imageFileList;
    }
    public void setCurrentMember(int cnt) {
        this.currentMember = cnt;

    }
}
