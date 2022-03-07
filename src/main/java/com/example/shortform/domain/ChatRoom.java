package com.example.shortform.domain;

import com.example.shortform.dto.resonse.ChatRoomListResponseDto;
import com.example.shortform.dto.resonse.ChatRoomResponseDto;
import com.example.shortform.dto.resonse.MemberResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class ChatRoom extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "room_name", nullable = false)
    private String roomName;

    @Column(name = "room_image")
    private String roomImage;

    @OneToMany(mappedBy = "chatRoom", orphanRemoval = true)
    private List<UserChatRoom> userChatRooms = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    public ChatRoomListResponseDto toResponseList(String username,
                                                  List<MemberResponseDto> memberList,
                                                  LocalDateTime createdAt,
                                                  LocalDateTime modifiedAt) {
        return ChatRoomListResponseDto.builder()
                .roomId(id)
                .roomName(roomName)
                .roomImage(roomImage)
                .username(username)
                .MemberList(memberList)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .build();
    }

    public ChatRoomResponseDto toRespose(MemberResponseDto member) {
        return ChatRoomResponseDto.builder()
                .roomId(id)
                .roomName(roomName)
                .roomImage(roomImage)
                .member(member)
                .build();
    }
}
