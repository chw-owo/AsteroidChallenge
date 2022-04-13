package com.example.shortform.controller;

import com.example.shortform.config.jwt.JwtAuthenticationProvider;
import com.example.shortform.domain.ChatMessage;
import com.example.shortform.domain.User;
import com.example.shortform.dto.request.ChatMessageRequestDto;
import com.example.shortform.dto.resonse.ChatMessageResponseDto;
import com.example.shortform.dto.resonse.ChatRoomMemberDto;
import com.example.shortform.exception.NotFoundException;
import com.example.shortform.repository.UserRepository;
import com.example.shortform.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatMessageService chatMessageService;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final UserRepository userRepository;

    // websocket으로 publish 된 메세지를 받는다
    @MessageMapping("/chat/message")
    public void message(@RequestBody ChatMessageRequestDto requestDto, @Header("authorization") String token) {
        String email = jwtAuthenticationProvider.getUser(token);
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException("인증되지 않은 유저입니다.")
        );

        chatMessageService.sendChatMessage(requestDto);

    }
}
