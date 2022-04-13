package com.example.shortform.pubsub;

import com.example.shortform.domain.ChatMessage;
import com.example.shortform.dto.resonse.ChatMessageResponseDto;
import com.example.shortform.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisSubscriber {
    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    public RedisSubscriber(ObjectMapper objectMapper, SimpMessageSendingOperations messagingTemplate, ChatMessageRepository chatMessageRepository) {
        this.objectMapper = objectMapper;
        this.messagingTemplate = messagingTemplate;
        this.chatMessageRepository = chatMessageRepository;
    }

//    convertAndSend 로 데이터를 보내면 여기서 잡아서 보낸다.
    public void sendMessage(String publishMessage) {
        try {
            ChatMessageResponseDto chatMessageResponseDto = objectMapper.readValue(publishMessage, ChatMessageResponseDto.class);
            messagingTemplate.convertAndSend("/sub/chat/rooms/" + chatMessageResponseDto.getRoomId(), chatMessageResponseDto);
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
    }
}
