package com.example.shortform.service;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.domain.ChatRoom;
import com.example.shortform.domain.User;
import com.example.shortform.domain.UserChatRoom;
import com.example.shortform.dto.request.ChatRoomRequestDto;
import com.example.shortform.dto.resonse.ChatRoomListResponseDto;
import com.example.shortform.dto.resonse.ChatRoomResponseDto;
import com.example.shortform.dto.resonse.MemberResponseDto;
import com.example.shortform.exception.NotFoundException;
import com.example.shortform.repository.ChatRoomRepository;
import com.example.shortform.repository.UserChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;

    public Long crateChatRoom(ChatRoomRequestDto requestDto, PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        ChatRoom chatRoom = requestDto.toEntity();
        chatRoomRepository.save(chatRoom);
        UserChatRoom userChatRoom = requestDto.toEntity(chatRoom, user);
        userChatRoomRepository.save(userChatRoom);
        return chatRoom.getId();
    }

    public List<ChatRoomListResponseDto> getAllMyRooms(PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        List<UserChatRoom> userChatRooms = userChatRoomRepository.findAllByUser(user);
        List<ChatRoomListResponseDto> chatRoomResponseDtoList = new ArrayList<>();

        for (UserChatRoom userChatRoom : userChatRooms) {
            List<MemberResponseDto> memberList = new ArrayList<>();
            ChatRoom chatRoom = userChatRoom.getChatRoom();
            List<UserChatRoom> userList = userChatRoomRepository.findAllByChatRoom(chatRoom);
            for (UserChatRoom room : userList) {
                User member = room.getUser();
                memberList.add(member.toMemberResponse());
            }
            ChatRoomListResponseDto chatRoomResponseDto = chatRoom.toResponseList(
                    user.getNickname(),
                    memberList,
                    chatRoom.getCreatedAt(),
                    chatRoom.getModifiedAt()
            );
            chatRoomResponseDtoList.add(chatRoomResponseDto);
        }
        return chatRoomResponseDtoList;
    }

    public ChatRoomResponseDto getRoom(Long roomId, PrincipalDetails principalDetails) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new NotFoundException("채팅방이 존재하지 않습니다.")
        );

        User user = principalDetails.getUser();
        MemberResponseDto member = user.toMemberResponse();

        ChatRoomResponseDto chatRoomResponseDto = chatRoom.toRespose(member);

        return chatRoomResponseDto;
    }
}
