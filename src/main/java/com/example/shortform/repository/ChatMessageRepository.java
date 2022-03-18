package com.example.shortform.repository;

import com.example.shortform.domain.ChatMessage;
import com.example.shortform.domain.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByChatRoom(ChatRoom chatRoom);

    Page<ChatMessage> findAllByChatRoom(ChatRoom chatRoom, Pageable pageable);
}