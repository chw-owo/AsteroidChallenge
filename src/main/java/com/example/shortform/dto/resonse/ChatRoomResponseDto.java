package com.example.shortform.dto.resonse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatRoomResponseDto {

    private Long roomId;
    private String roomName;
    private String roomImage;
    private MemberResponseDto member;
}
