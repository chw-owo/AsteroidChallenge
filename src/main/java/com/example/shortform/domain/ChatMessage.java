package com.example.shortform.domain;

import com.example.shortform.dto.resonse.ChatMessageResponseDto;
import com.example.shortform.dto.resonse.ChatRoomMemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class ChatMessage extends Timestamped{
    public enum MessageType{
        ENTER,
        TALK,
        QUIT
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(name = "room_id", nullable = false)
    private String roomId;

    public ChatMessageResponseDto toResponse(String createdAt) {
        return ChatMessageResponseDto.builder()
                .message(content)
                .roomId(roomId)
                .createdAt(createdAt)
                .type(type)
                .sender(user.getNickname())
                .id(id)
                .user(user.toChatMemberResponse())
                .build();
    }

}
