package com.example.shortform.service;

import com.example.shortform.domain.ChatMessage;
import com.example.shortform.domain.User;
import com.example.shortform.dto.request.ChatMessageRequestDto;
import com.example.shortform.exception.NotFoundException;
import com.example.shortform.repository.ChatMessageRepository;
import com.example.shortform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

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

    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        System.out.println("룸 아이디 destination" + destination);

        if (lastIndex != -1) {
            return (destination.substring(lastIndex + 1));
        } else {
            return null;
        }
    }

    public ChatMessage saveDB(ChatMessageRequestDto requestDto, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException("유저정보가 존재하지 않습니다.")
        );

        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm");
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateResult = sdf.format(date);
        requestDto.setCreatedAt(dateResult);

        if (ChatMessage.MessageType.ENTER.equals(requestDto.getType())) {
            requestDto.setMessage(user.getNickname() + "님이 방에 입장했습니다.");
        } else if (ChatMessage.MessageType.QUIT.equals(requestDto.getType())) {
            requestDto.setMessage(user.getNickname() + "님이 방에서 나갔습니다.");
        }

        ChatMessage chatMessage = requestDto.toEntity(user);
        return chatMessageRepository.save(chatMessage);
    }

    public void sendChatMessage(ChatMessage chatMessage) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }
}
