package com.example.shortform.controller;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.dto.request.ChatRoomRequestDto;
import com.example.shortform.dto.resonse.ChatMessageListDto;
import com.example.shortform.dto.resonse.ChatMessageResponseDto;
import com.example.shortform.dto.resonse.ChatRoomListResponseDto;
import com.example.shortform.dto.resonse.ChatRoomResponseDto;
import com.example.shortform.exception.UnauthorizedException;
import com.example.shortform.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    // 채팅 방 생성 API
    @PostMapping("/chat/rooms")
    public HashMap<String, Object> createChatRoom(@RequestBody ChatRoomRequestDto requestDto,
                                  @AuthenticationPrincipal PrincipalDetails principalDetails) {
        // 로그인 한 유저만 가능하도록 설정
        if (principalDetails != null) {
            chatRoomService.createChatRoom(requestDto, principalDetails);
            HashMap<String, Object> result = new HashMap<>();
            result.put("result", "true");
            return result;
        } else {
            throw new UnauthorizedException("로그인 후 이용가능합니다.");
        }
    }

    // 내가 참여 가능한 채팅 방 목록 조회 API
    @GetMapping("/chat/rooms")
    public List<ChatRoomListResponseDto> getAllMyRooms(@AuthenticationPrincipal PrincipalDetails principalDetails) throws ParseException {
        // 로그인 한 유저만 이용가능하도록 설정
        if (principalDetails != null)
            return chatRoomService.getAllMyRooms(principalDetails);
        else
            throw new UnauthorizedException("로그인 후 이용가능합니다.");
    }

    // 이전 메세지 조회 API
    @GetMapping("/chat/rooms/{roomId}/messages")
    public ChatMessageListDto getAllMessages(@PathVariable Long roomId,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails,
                                             @PageableDefault(size = 500, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        // 로그인 한 유저만 이용가능하도록 설정
        if (principalDetails != null)
            return chatRoomService.getAllMessages(roomId, principalDetails, pageable);
        else
            throw new UnauthorizedException("로그인 후 이용가능합니다.");
    }

    @DeleteMapping("/chat/rooms/{roomId}")
    public HashMap<String, Object> deleteRoom(@PathVariable Long roomId,
                                              @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails != null) {
            chatRoomService.deleteRoom(roomId, principalDetails);
            HashMap<String, Object> result = new HashMap<>();
            result.put("result", "true");
            return result;
        } else {
            throw new UnauthorizedException("로그인 후 이용가능합니다.");
        }
    }
}
