package com.example.shortform.controller;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.dto.request.ChatRoomRequestDto;
import com.example.shortform.dto.resonse.ChatMessageListDto;
import com.example.shortform.dto.resonse.ChatMessageResponseDto;
import com.example.shortform.dto.resonse.ChatRoomListResponseDto;
import com.example.shortform.dto.resonse.ChatRoomResponseDto;
import com.example.shortform.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping("/chat/rooms")
    public HashMap<String, Object> createChatRoom(@RequestBody ChatRoomRequestDto requestDto,
                                  @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long roomId = chatRoomService.createChatRoom(requestDto, principalDetails);
        HashMap<String, Object> result = new HashMap<>();
        result.put("result", "true");
        return result;
    }

    @GetMapping("/chat/rooms")
    public List<ChatRoomListResponseDto> getAllMyRooms(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return chatRoomService.getAllMyRooms(principalDetails);
    }

    @GetMapping("/chat/rooms/{roomId}/messageses")
    public ChatRoomResponseDto getRoom(@PathVariable Long roomId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return chatRoomService.getRoom(roomId, principalDetails);
    }

    @GetMapping("/chat/rooms/{roomId}/messages")
    public ChatMessageListDto getAllMessages(@PathVariable Long roomId,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return chatRoomService.getAllMessages(roomId, principalDetails);
    }

    @DeleteMapping("/chat/rooms/{roomId}")
    public HashMap<String, Object> deleteRoom(@PathVariable Long roomId,
                                              @AuthenticationPrincipal PrincipalDetails principalDetails) {
        chatRoomService.deleteRoom(roomId, principalDetails);
        HashMap<String, Object> result = new HashMap<>();
        result.put("result", "true");
        return result;
    }
}
