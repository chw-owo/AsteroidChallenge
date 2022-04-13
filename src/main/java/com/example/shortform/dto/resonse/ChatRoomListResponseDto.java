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
public class ChatRoomListResponseDto {
    private Long roomId;
    private String chatRoomName;
    private List<String> chatRoomImg;
    private String recentMessage;
    private int currentMember;
    private List<ChatRoomMemberDto> user;
    private String createdAt;
}
