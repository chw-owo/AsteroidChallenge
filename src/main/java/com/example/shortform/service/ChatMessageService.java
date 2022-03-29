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

import javax.transaction.Transactional;
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

    // 헤더에서 추출한 destination을 이용해 roomId 값 리턴
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

        // message type에 따라 알맞은 메세지 세팅
        if (ChatMessage.MessageType.ENTER.equals(requestDto.getType())) {
            requestDto.setMessage(user.getNickname() + "님이 방에 입장했습니다.");
        } else if (ChatMessage.MessageType.QUIT.equals(requestDto.getType())) {
            requestDto.setMessage(user.getNickname() + "님이 방에서 퇴장했습니다.");
        }

        // DB 저장을 위한 메서드
        ChatMessageResponseDto responseDto = save(requestDto, user);

        redisTemplate.convertAndSend(channelTopic.getTopic(), responseDto);
    }


    @Transactional
    public ChatMessageResponseDto save(ChatMessageRequestDto requestDto, User user) {

        // 메세지 생성 시간 삽입
        LocalDateTime now = LocalDateTime.now();
        String create = now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
        requestDto.setCreatedAt(create);

        // roomId를 이용해 채팅 방 유무 확인
        ChatRoom chatRoom = chatRoomRepository.findById(Long.valueOf(requestDto.getRoomId())).orElseThrow(
                () -> new NotFoundException("채팅방이 존재하지 않습니다.")
        );

        UserChatRoom userChatRoom = userChatRoomRepository.findByChatRoomAndUser(chatRoom, user);
        // user 정보와 채팅방 정보를 이용해 채팅 방에 등록된 유저인지 확인
        // 등록되지 않은 유저일시 DB에 저장
        if (userChatRoom == null) {
            userChatRoom = UserChatRoom.builder()
                    .user(user)
                    .chatRoom(chatRoom)
                    .build();
            userChatRoomRepository.save(userChatRoom);
        }

        ChatMessageResponseDto responseDto;
        // message type이 TALK 인 경우 채팅 메세지 DB에 저장 후 responseDto 변환
        // 아닐 경우는 DB에 저장하지 않고 responseDto 변환
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
