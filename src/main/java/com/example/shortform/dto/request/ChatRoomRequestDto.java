package com.example.shortform.dto.request;

import com.example.shortform.domain.ChatRoom;
import com.example.shortform.domain.User;
import com.example.shortform.domain.UserChatRoom;
import lombok.Data;

@Data
public class ChatRoomRequestDto {
    String roomName;
    String image;

    public ChatRoom toEntity() {
        return ChatRoom.builder()
                .roomImage(roomName)
                .roomName(roomName)
                .build();
    }

    public UserChatRoom toEntity(ChatRoom chatRoom, User user) {
        return UserChatRoom.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();
    }
}
