package com.example.shortform.service;

import com.example.shortform.domain.ChatMessage;
import com.example.shortform.domain.ChatRoom;
import com.example.shortform.domain.User;
import com.example.shortform.domain.UserChatRoom;
import com.example.shortform.dto.request.ChatMessageRequestDto;
import com.example.shortform.dto.resonse.ChatMessageResponseDto;
import com.example.shortform.dto.resonse.CommentResponseDto;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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

        ChatMessageResponseDto responseDto = save(requestDto, user);

        redisTemplate.convertAndSend(channelTopic.getTopic(), responseDto);
    }


    @Transactional
    public ChatMessageResponseDto save(ChatMessageRequestDto requestDto, User user) {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateResult = sdf.format(date);
        requestDto.setCreatedAt(dateResult);

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
            responseDto = chatMessage.toResponse(chatMessage.getCreatedAt().toString());
        } else {
            responseDto = requestDto.toMessageResponse(user.toChatMemberResponse());
        }

        return responseDto;

    }
}

//    @Transactional
//    public ChatMessage saveMessage(ChatMessageRequestDto requestDto, String email) {
//        User user = userRepository.findByEmail(email).orElseThrow(
//                () -> new NotFoundException("유저정보가 존재하지 않습니다.")
//        );
//
//        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
//        Calendar cal = Calendar.getInstance();
//        Date date = cal.getTime();
//        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
//        String dateResult = sdf.format(date);
//        requestDto.setCreatedAt(dateResult);
//
//        ChatRoom chatRoom = chatRoomRepository.findById(Long.valueOf(requestDto.getRoomId())).orElseThrow(
//                () -> new NotFoundException("")
//        );
//        ChatMessage chatMessage = requestDto.toEntity(user, chatRoom);
//        UserChatRoom userChatRoom = userChatRoomRepository.findByChatRoomAndUser(chatRoom, user);
//        if (userChatRoom == null) {
//            userChatRoom = UserChatRoom.builder()
//                    .user(user)
//                    .chatRoom(chatRoom)
//                    .build();
//            userChatRoomRepository.save(userChatRoom);
//        }
//
//        return chatMessageRepository.save(chatMessage);
//    }

//    public void saveChatMember(ChatMessageRequestDto requestDto, User user) {
//
//        ChatRoom chatRoom = chatRoomRepository.findById(Long.valueOf(requestDto.getRoomId())).orElseThrow(
//                () -> new NotFoundException("")
//        );
//        UserChatRoom userChatRoom = userChatRoomRepository.findByChatRoomAndUser(chatRoom, user);
//        if (userChatRoom == null) {
//            userChatRoom = UserChatRoom.builder()
//                    .user(user)
//                    .chatRoom(chatRoom)
//                    .build();
//            userChatRoomRepository.save(userChatRoom);
//        }
//    }

//    public void sendChatMessages(ChatMessageRequestDto requestDto) {
//        User user = userRepository.findById(requestDto.getUserId()).orElseThrow(
//                () -> new NotFoundException("")
//        );
////        ChatRoom chatRoom = chatRoomRepository.findById(Long.valueOf(requestDto.getRoomId())).orElse(null);
//
////        UserChatRoom userChatRoom = userChatRoomRepository.findByChatRoomAndUser(chatRoom, user);
//
//        if (ChatMessage.MessageType.ENTER.equals(requestDto.getType())) {
//            saveChatMember(requestDto, user);
//            requestDto.setMessage(user.getNickname() + "님이 방에 입장했습니다.");
//
//            SimpleDateFormat sdf = new SimpleDateFormat("YYYY.MM.dd HH:mm:ss");
//            Calendar cal = Calendar.getInstance();
//            Date date = cal.getTime();
//            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
//            String dateResult = sdf.format(date);
//            requestDto.setCreatedAt(dateResult);
//
//            ChatMessageResponseDto responseDto = requestDto.toMessageResponse(user.toChatMemberResponse());
//            redisTemplate.convertAndSend(channelTopic.getTopic(), responseDto);
//        } else if (ChatMessage.MessageType.QUIT.equals(requestDto.getType())) {
//            saveChatMember(requestDto, user);
//            requestDto.setMessage(user.getNickname() + "님이 방에서 나갔습니다.");
//
//            SimpleDateFormat sdf = new SimpleDateFormat("YYYY.MM.dd HH:mm:ss");
//            Calendar cal = Calendar.getInstance();
//            Date date = cal.getTime();
//            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
//            String dateResult = sdf.format(date);
//            requestDto.setCreatedAt(dateResult);
//
//            ChatMessageResponseDto responseDto = requestDto.toMessageResponse(user.toChatMemberResponse());
//            redisTemplate.convertAndSend(channelTopic.getTopic(), responseDto);
//        } else {
//            ChatMessage chatMessage = saveMessage(requestDto, user.getEmail());
//            String createdAt = chatMessage.getCreatedAt().toString();
//            String year = createdAt.substring(0,4) + ".";
//            String month = createdAt.substring(5,7) + ".";
//            String day = createdAt.substring(8,10) + " ";
//            String time = createdAt.substring(11,19);
//            createdAt = year + month + day + time;
//            requestDto.setCreatedAt(createdAt);
//            ChatMessageResponseDto responseDto = requestDto.toMessageResponse(user.toChatMemberResponse());
//            responseDto.setId(chatMessage.getId());
//            redisTemplate.convertAndSend(channelTopic.getTopic(), responseDto);
//        }

//    }
