package com.example.shortform.handler;

import com.example.shortform.config.jwt.JwtAuthenticationProvider;
import com.example.shortform.domain.ChatMessage;
import com.example.shortform.domain.User;
import com.example.shortform.exception.NotFoundException;
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

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@Transactional
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final RedisRepository redisRepository;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        log.info("Web Socket 들어올 때 token 검증 = {}", message.getHeaders());
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            String jwtToken = accessor.getFirstNativeHeader("Authorization");
            log.info("Web Socket 들어올 때 token 검증 = {}", jwtToken);
            jwtAuthenticationProvider.validateToken(jwtToken);

            if (jwtToken == null) {
                throw new UnauthorizedException("로그인 후 이용가능합니다.");
            }

        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            String destination = Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId");
            String roomId = chatMessageService.getRoomId(destination);

            if (roomId != null) {
                String sessionId = (String) message.getHeaders().get("simpSessionId");
                String username = jwtAuthenticationProvider.getUser(accessor.getFirstNativeHeader("Authorization"));
                User user = userRepository.findByEmail(username).orElseThrow(
                        () -> new NotFoundException("")
                );

                redisRepository.setUserEnterInfo(sessionId, roomId);

                chatMessageService.sendChatMessage(ChatMessage.builder()
                        .roomId(roomId)
                        .user(user)
                        .type(ChatMessage.MessageType.ENTER)
                        .build());
            }
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            String roomId = redisRepository.getUserEnterRoomId(sessionId);

            redisRepository.removeUserEnterInfo(sessionId);
        }

        return message;
    }

}
