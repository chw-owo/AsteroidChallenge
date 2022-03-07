package com.example.shortform.dto.request;

import com.example.shortform.domain.ChatMessage;
import com.example.shortform.domain.MessageType;
import com.example.shortform.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageRequestDto {
    private MessageType type;
    private String roomId;
    private Long userId;
    private String message;
    private String createdAt;

    public ChatMessage toEntity(User user) {
        return ChatMessage.builder()
                .content(message)
                .user(user)
                .roomId(roomId)
                .build();
    }
}
