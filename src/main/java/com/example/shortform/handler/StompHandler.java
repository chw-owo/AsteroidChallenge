//package com.example.shortform.handler;
//
//import com.example.shortform.config.jwt.JwtAuthenticationProvider;
//import com.example.shortform.exception.UnauthorizedException;
//import com.example.shortform.service.ChatMessageService;
//import com.example.shortform.service.ChatRoomService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.stomp.StompCommand;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.stereotype.Component;
//
//import javax.transaction.Transactional;
//import java.util.Optional;
//
//@Transactional
//@Component
//@RequiredArgsConstructor
//public class StompHandler implements ChannelInterceptor {
//
//    private final JwtAuthenticationProvider jwtAuthenticationProvider;
//    private final ChatRoomService chatRoomService;
//    private final ChatMessageService chatMessageService;
//
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//
//        if (StompCommand.CONNECT == accessor.getCommand()) {
//            String jwtToken = accessor.getFirstNativeHeader("Authorization");
//            jwtAuthenticationProvider.validateToken(jwtToken);
//
//            if (jwtToken == null) {
//                throw new UnauthorizedException("로그인 후 이용가능합니다.");
//            }
//
//        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
//            String destination = Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId");
//            String destination2 = (String) accessor.getHeader("simpDestination");
//        }
//
//        return message;
//    }
//
//}
