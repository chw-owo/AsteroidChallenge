//package com.example.shortform.controller;
//
//import com.example.shortform.config.jwt.JwtAuthenticationProvider;
//import com.example.shortform.domain.User;
//import com.example.shortform.service.ChatMessageService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//public class ChatMessageController {
//    private final ChatMessageService chatMessageService;
//    private final JwtAuthenticationProvider jwtAuthenticationProvider;
//
//    @MessageMapping("chat/message")
//    public void message(@Header("") String token) {
//
//    }
//}
