package com.example.shortform.repository;

import com.example.shortform.domain.ChatMessage;
import com.example.shortform.domain.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    ChatMessage findFirstByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);

    @Query(value = "select distinct m from ChatMessage m " +
            "inner join m.chatRoom.challenge " +
            "inner join m.user" +
            " where m.chatRoom = :chatRoom",
            countQuery = "select count(m) from ChatMessage m " +
                    "where m.chatRoom = :chatRoom")
    Page<ChatMessage> findAllChatRoomMessage(ChatRoom chatRoom, Pageable pageable);
}