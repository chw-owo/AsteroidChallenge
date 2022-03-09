package com.example.shortform.dto.request;

import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.ChatRoom;
import com.example.shortform.domain.User;
import com.example.shortform.domain.UserChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomRequestDto {
    private Long challengeId;

    public ChatRoom toEntity(String profileImage, Challenge challenge) {
        return ChatRoom.builder()
                .roomImage(profileImage)
                .challenge(challenge)
                .build();
    }

    public UserChatRoom toEntity(ChatRoom chatRoom, User user) {
        return UserChatRoom.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();
    }
}
