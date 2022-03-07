package com.example.shortform.dto.resonse;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ChatRoomListResponseDto {
    private Long roomId;
    private String roomName;
    private String roomImage;
    private List<MemberResponseDto> MemberList;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
