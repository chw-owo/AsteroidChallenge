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

    // controller에 도착하기 전에 Intercept하여 먼저 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        //accessor를 사용하면 패킷에 접근이 가능
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String jwtToken = accessor.getFirstNativeHeader("authorization");
//        jwtAuthenticationProvider.validateToken(jwtToken);
        //log.info("Web Socket 들어올 때 token 검증 = {}", message.getHeaders());
        //log.info("Web Socket 들어올 때 token 검증 = {}", jwtToken);

        // Command 헤더가 CONNECT일 경우 TCP 연결
        if (StompCommand.CONNECT == accessor.getCommand()) {
           // log.info("connect 진입");
            //log.info("Connect 시 email= {}", jwtAuthenticationProvider.getUser(accessor.getFirstNativeHeader("authorization")));
            if (jwtToken == null) {
                throw new UnauthorizedException("로그인 후 이용가능합니다.");
            }

            // Command 헤더가 SUBSCRIBE일 경우 채팅 방 구독 요청
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            //log.info("Subscribe token 검증 = {}", jwtToken);
            // 메세지 헤더에서 destination 추출
            String destination = Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId");
            // 추출한 destination을 이용해 roomId 추출
            String roomId = chatMessageService.getRoomId(destination);
            //log.info("sub destination roomId = {} {}", destination, roomId);

            if (roomId != null) {
               // log.info("sub if문 진입");
                //메세지 헤더에서 sessionId 추출
                String sessionId = (String) message.getHeaders().get("simpSessionId");
                //log.info("sub sessionId = {}", sessionId);
                // 토큰에서 유저 정보 추출
//                String email = jwtAuthenticationProvider.getUser(accessor.getFirstNativeHeader("authorization"));
//                //log.info("sub email = {}", email);
//                User user = userRepository.findByEmail(email).orElseThrow(
//                        () -> new NotFoundException("")
//                );

                // redis 에 sessionId와 roomId 매핑
                redisRepository.setUserEnterInfo(sessionId, roomId);

//                ChatMessageRequestDto requestDto = ChatMessageRequestDto.builder()
//                        .type(ChatMessage.MessageType.ENTER)
//                        .roomId(roomId)
//                        .userId(user.getId())
//                        .build();
//
//                chatMessageService.sendChatMessage(requestDto);
                //log.info("SUBSCRIBED {}, {}", user.getNickname(), roomId);
            }
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            //log.info("disconnect 진입 token = {}", jwtToken);
            //헤더에서 sessionId 추출
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            //sessionId를 이용해 redis 에 매핑된 roomId를 찾는다.
            String roomId = redisRepository.getUserEnterRoomId(sessionId);

            //log.info("sessionId = {}, roomId = {}", sessionId, roomId);

            String token = Optional.ofNullable(accessor.getFirstNativeHeader("authorization")).orElse("unknownUser");
            //log.info("token = {}", token);

            if (accessor.getFirstNativeHeader("authorization") != null) {
                // 토큰에서 유저 정보 추출
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
                // redis 에서 sessionId와 매핑했던 roomId 제거
                redisRepository.removeUserEnterInfo(sessionId);
            }

            //log.info("DISCONNECT {}, {}", sessionId, roomId);
        }

        return message;
    }

}
