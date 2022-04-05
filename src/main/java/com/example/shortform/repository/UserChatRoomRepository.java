package com.example.shortform.repository;

import com.example.shortform.domain.ChatRoom;
import com.example.shortform.domain.User;
import com.example.shortform.domain.UserChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, Long> {
    List<UserChatRoom> findAllByUser(User user);

    List<UserChatRoom> findAllByChatRoom(ChatRoom chatRoom);

    UserChatRoom findByChatRoomAndUser(ChatRoom chatRoom, User user);

    void deleteByChatRoomIdAndUserId(Long id, Long id1);

    @Query("select distinct uc from UserChatRoom uc where uc.chatRoom = :chatRoom")
    List<UserChatRoom> findAllChatRoomUser(ChatRoom chatRoom);
}