package com.example.shortform.config;

import com.example.shortform.handler.StompHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    @Autowired
    public WebSocketConfig(StompHandler stompHandler) {
        this.stompHandler = stompHandler;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //prefix /sub로 메세지 구분
        registry.enableSimpleBroker("/sub");
        //prefix /pub로 메세지 발송을 요청
        // /pub 요청 시 massagemapping으로 이동
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chatting") // websocket 연결 주소
                .setAllowedOrigins("http://localhost:3000", "https://www.sohangsung.co.kr/", "https://sohangsung.co.kr/")
                .withSockJS(); // 닞은 버전의 브라우저에서도 websocket이 동작하게 하는 역할
    }

    // interceptor 설정
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // stomphandler가 미리 token 및 message type 확인 할수 있도록 interceptor 설정
        registration.interceptors(stompHandler);
    }
}
