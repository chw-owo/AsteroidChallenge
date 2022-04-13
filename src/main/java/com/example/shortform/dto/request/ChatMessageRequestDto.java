package com.example.shortform.dto.request;

import com.example.shortform.domain.ChatMessage;
import com.example.shortform.domain.ChatRoom;
import com.example.shortform.domain.User;
import com.example.shortform.dto.resonse.ChatMessageResponseDto;
import com.example.shortform.dto.resonse.ChatRoomMemberDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatMessageRequestDto {

    private ChatMessage.MessageType type;
    private String roomId;
    private Long userId;
    private String message;
    private String createdAt;

    public ChatMessage toEntity(User user, ChatRoom chatRoom) {
        return ChatMessage.builder()
                .type(type)
                .roomId(roomId)
                .content(message)
                .user(user)
                .chatRoom(chatRoom)
                .roomId(roomId)
                .build();
    }

    public ChatMessageResponseDto toMessageResponse(ChatRoomMemberDto user) {
        return ChatMessageResponseDto.builder()
                .type(type)
                .user(user)
                .sender(user.getNickname())
                .message(message)
                .createdAt(createdAt)
                .roomId(roomId)
                .build();
    }
}
