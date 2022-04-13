package com.example.shortform.service;

import com.example.shortform.domain.ChatMessage;
import com.example.shortform.domain.ChatRoom;
import com.example.shortform.domain.User;
import com.example.shortform.domain.UserChatRoom;
import com.example.shortform.dto.request.ChatMessageRequestDto;
import com.example.shortform.dto.resonse.ChatMessageResponseDto;
import com.example.shortform.exception.NotFoundException;
import com.example.shortform.repository.ChatMessageRepository;
import com.example.shortform.repository.ChatRoomRepository;
import com.example.shortform.repository.UserChatRoomRepository;
import com.example.shortform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;

    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        System.out.println("룸 아이디 destination" + destination);

        if (lastIndex != -1) {
            return (destination.substring(lastIndex + 1));
        } else {
            return null;
        }
    }

    public void sendChatMessage(ChatMessageRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId()).orElseThrow(
                () -> new NotFoundException("유저가 존재하지 않습니다.")
        );

        if (ChatMessage.MessageType.ENTER.equals(requestDto.getType())) {
            requestDto.setMessage(user.getNickname() + "님이 방에 입장했습니다.");
        } else if (ChatMessage.MessageType.QUIT.equals(requestDto.getType())) {
            requestDto.setMessage(user.getNickname() + "님이 방에서 퇴장했습니다.");
        }

        ChatMessageResponseDto responseDto = saveMessage(requestDto, user);

        redisTemplate.convertAndSend(channelTopic.getTopic(), responseDto);
    }


    @Transactional
    public ChatMessageResponseDto saveMessage(ChatMessageRequestDto requestDto, User user) {

        LocalDateTime now = LocalDateTime.now();
        String create = now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
        requestDto.setCreatedAt(create);

        ChatRoom chatRoom = chatRoomRepository.findById(Long.valueOf(requestDto.getRoomId())).orElseThrow(
                () -> new NotFoundException("채팅방이 존재하지 않습니다.")
        );

        UserChatRoom userChatRoom = userChatRoomRepository.findByChatRoomAndUser(chatRoom, user);
        if (userChatRoom == null) {
            userChatRoom = UserChatRoom.builder()
                    .user(user)
                    .chatRoom(chatRoom)
                    .build();
            userChatRoomRepository.save(userChatRoom);
        }

        ChatMessageResponseDto responseDto;

        if (ChatMessage.MessageType.TALK.equals(requestDto.getType())) {
            ChatMessage chatMessage = chatMessageRepository.save(requestDto.toEntity(user, chatRoom));
            String createAt = chatMessage.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
            responseDto = chatMessage.toResponse(createAt);
        } else {
            responseDto = requestDto.toMessageResponse(user.toChatMemberResponse());
        }

        return responseDto;

    }
}
