package com.example.shortform.dto.resonse;

import com.example.shortform.domain.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponseDto {
    private ChatMessage.MessageType type;
    private Long id;
    private String roomId;
    private String message;
    private String createdAt;
    private ChatRoomMemberDto user;
    private String sender;
}
