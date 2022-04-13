package com.example.shortform.handler;

import com.example.shortform.config.jwt.JwtAuthenticationProvider;
import com.example.shortform.exception.UnauthorizedException;
import com.example.shortform.repository.RedisRepository;
import com.example.shortform.repository.UserRepository;
import com.example.shortform.service.ChatMessageService;
import com.example.shortform.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final ChatMessageService chatMessageService;
    private final RedisRepository redisRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String jwtToken = accessor.getFirstNativeHeader("authorization");

        if (StompCommand.CONNECT == accessor.getCommand()) {
            if (jwtToken == null) {
                throw new UnauthorizedException("로그인 후 이용가능합니다.");
            }

        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            String destination = Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId");
            String roomId = chatMessageService.getRoomId(destination);
            if (roomId != null) {
                String sessionId = (String) message.getHeaders().get("simpSessionId");
                redisRepository.setUserEnterInfo(sessionId, roomId);

            }
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            String roomId = redisRepository.getUserEnterRoomId(sessionId);
            if (roomId != null) {
                redisRepository.removeUserEnterInfo(sessionId);
            }
        }

        return message;
    }

}
