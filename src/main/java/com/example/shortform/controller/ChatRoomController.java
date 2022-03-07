package com.example.shortform.controller;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.dto.request.ChatRoomRequestDto;
import com.example.shortform.dto.resonse.ChatRoomListResponseDto;
import com.example.shortform.dto.resonse.ChatRoomResponseDto;
import com.example.shortform.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping("/rooms")
    public void createChatRoom(@RequestBody ChatRoomRequestDto requestDto,
                               @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long roomId = chatRoomService.crateChatRoom(requestDto, principalDetails);
    }

    @GetMapping("/myrooms")
    public List<ChatRoomListResponseDto> getAllMyRooms(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return chatRoomService.getAllMyRooms(principalDetails);
    }

    @GetMapping("/rooms/{roomId}")
    public ChatRoomResponseDto getRoom(@PathVariable Long roomId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return chatRoomService.getRoom(roomId, principalDetails);
    }
}
