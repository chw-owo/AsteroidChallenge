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

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final RedisRepository redisRepository;
    private final UserRepository userRepository;

    // controller에 도착하기 전에 Intercept하여 먼저 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        //accessor를 사용하면 패킷에 접근이 가능
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String jwtToken = accessor.getFirstNativeHeader("authorization");


        // Command 헤더가 CONNECT일 경우 TCP 연결
        if (StompCommand.CONNECT == accessor.getCommand()) {

            if (jwtToken == null) {
                throw new UnauthorizedException("로그인 후 이용가능합니다.");
            }

            // Command 헤더가 SUBSCRIBE일 경우 채팅 방 구독 요청
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {

            // 메세지 헤더에서 destination 추출
            String destination = Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId");
            // 추출한 destination을 이용해 roomId 추출
            String roomId = chatMessageService.getRoomId(destination);


            if (roomId != null) {

                //메세지 헤더에서 sessionId 추출
                String sessionId = (String) message.getHeaders().get("simpSessionId");


                // redis 에 sessionId와 roomId 매핑
                redisRepository.setUserEnterInfo(sessionId, roomId);

            }
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {

            //헤더에서 sessionId 추출
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            //sessionId를 이용해 redis 에 매핑된 roomId를 찾는다.
            String roomId = redisRepository.getUserEnterRoomId(sessionId);

            if (roomId != null) {
                // redis 에서 sessionId와 매핑했던 roomId 제거
                redisRepository.removeUserEnterInfo(sessionId);
            }


        }

        return message;
    }

}
