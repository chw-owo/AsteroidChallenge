package com.example.shortform.repository;

import com.example.shortform.domain.ChatRoom;
import com.example.shortform.domain.User;
import com.example.shortform.domain.UserChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, Long> {
    List<UserChatRoom> findAllByUser(User user);

    List<UserChatRoom> findAllByChatRoom(ChatRoom chatRoom);
}