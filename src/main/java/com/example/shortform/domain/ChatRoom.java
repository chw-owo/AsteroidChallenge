package com.example.shortform.domain;

import com.example.shortform.dto.resonse.ChatRoomListResponseDto;
import com.example.shortform.dto.resonse.ChatRoomMemberDto;
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

//    @Column(name = "room_name", nullable = false)
//    private String roomName;

    @Column(name = "room_image")
    private String roomImage;

    @OneToMany(mappedBy = "chatRoom", orphanRemoval = true)
    private List<UserChatRoom> userChatRooms = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();

    @OneToOne(mappedBy = "chatRoom")
    private Challenge challenge;

    public ChatRoomListResponseDto toResponseList(LocalDateTime createdAt,
                                                  List<String> imageList,
                                                  int currentMember,
                                                  List<ChatRoomMemberDto> roomMemberDto,
                                                  String message) {
        return ChatRoomListResponseDto.builder()
                .roomId(id)
                .chatRoomName(challenge.getTitle())
                .chatRoomImg(imageList)
                .currentMember(currentMember)
                .createdAt(createdAt.toString())
                .recentMessage(message)
                .user(roomMemberDto)
                .build();
    }

    public ChatRoomResponseDto toRespnose(MemberResponseDto member) {
        return ChatRoomResponseDto.builder()
                .roomId(id)
                .roomName(challenge.getTitle())
                .roomImage(roomImage)
                .member(member)
                .build();
    }
}
