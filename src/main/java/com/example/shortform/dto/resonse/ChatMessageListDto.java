package com.example.shortform.dto.resonse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageListDto {
    private String roomName;
    private Long roomId;
    private int currentMember;
    private boolean next;
    private List<ChatMessageResponseDto> messageList;

//    public ChatMessageListDto(String roomName, String roomId, int currentMember) {
//
//    }
}
