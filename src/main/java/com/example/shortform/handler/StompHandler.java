package com.example.shortform.handler;

import com.example.shortform.config.jwt.JwtAuthenticationProvider;
import com.example.shortform.domain.ChatMessage;
import com.example.shortform.domain.User;
import com.example.shortform.dto.request.ChatMessageRequestDto;
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
//@Transactional
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

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String jwtToken = accessor.getFirstNativeHeader("authorization");
//        jwtAuthenticationProvider.validateToken(jwtToken);
        //log.info("Web Socket 들어올 때 token 검증 = {}", message.getHeaders());
        //log.info("Web Socket 들어올 때 token 검증 = {}", jwtToken);

        if (StompCommand.CONNECT == accessor.getCommand()) {
           // log.info("connect 진입");
            //log.info("Connect 시 email= {}", jwtAuthenticationProvider.getUser(accessor.getFirstNativeHeader("authorization")));
            if (jwtToken == null) {
                throw new UnauthorizedException("로그인 후 이용가능합니다.");
            }

        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            //log.info("Subscribe token 검증 = {}", jwtToken);
            String destination = Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId");
            String roomId = chatMessageService.getRoomId(destination);
            //log.info("sub destination roomId = {} {}", destination, roomId);

            if (roomId != null) {
               // log.info("sub if문 진입");
                String sessionId = (String) message.getHeaders().get("simpSessionId");
                //log.info("sub sessionId = {}", sessionId);
                String email = jwtAuthenticationProvider.getUser(accessor.getFirstNativeHeader("authorization"));
                //log.info("sub email = {}", email);
                User user = userRepository.findByEmail(email).orElseThrow(
                        () -> new NotFoundException("")
                );

                redisRepository.setUserEnterInfo(sessionId, roomId);

                ChatMessageRequestDto requestDto = ChatMessageRequestDto.builder()
                        .type(ChatMessage.MessageType.ENTER)
                        .roomId(roomId)
                        .userId(user.getId())
                        .build();

                chatMessageService.sendChatMessage(requestDto);
                //log.info("SUBSCRIBED {}, {}", user.getNickname(), roomId);
            }
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            //log.info("disconnect 진입 token = {}", jwtToken);
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            String roomId = redisRepository.getUserEnterRoomId(sessionId);

            //log.info("sessionId = {}, roomId = {}", sessionId, roomId);

            String token = Optional.ofNullable(accessor.getFirstNativeHeader("authorization")).orElse("unknownUser");
            //log.info("token = {}", token);

            if (accessor.getFirstNativeHeader("authorization") != null) {
                String email = jwtAuthenticationProvider.getUser(token);
                //log.info("email = {}", email);
                User user = userRepository.findByEmail(email).orElseThrow(
                        () -> new NotFoundException("로그인 하지 않은 유저입니다.")
                );
                ChatMessageRequestDto requestDto = ChatMessageRequestDto.builder()
                        .type(ChatMessage.MessageType.QUIT)
                        .roomId(roomId)
                        .userId(user.getId())
                        .build();
                chatMessageService.sendChatMessage(requestDto);

            }

            if (roomId != null) {
                redisRepository.removeUserEnterInfo(sessionId);
            }

            //log.info("DISCONNECT {}, {}", sessionId, roomId);
        }

        return message;
    }

}
