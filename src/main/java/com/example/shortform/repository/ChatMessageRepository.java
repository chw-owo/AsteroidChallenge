package com.example.shortform.repository;

import com.example.shortform.domain.ChatMessage;
import com.example.shortform.domain.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByChatRoom(ChatRoom chatRoom);

    ChatMessage findFirstByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);

    Page<ChatMessage> findAllByChatRoom(ChatRoom chatRoom, Pageable pageable);

    @Query(value = "select distinct m from ChatMessage m inner join m.chatRoom.challenge inner join m.user where m.chatRoom = :chatRoom",
    countQuery = "select count(m) from ChatMessage m where m.chatRoom = :chatRoom")
    Page<ChatMessage> findAllChatRoomMessage(ChatRoom chatRoom, Pageable pageable);
}