package com.example.shortform.service;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.domain.*;
import com.example.shortform.dto.request.ChatRoomRequestDto;
import com.example.shortform.dto.resonse.*;
import com.example.shortform.exception.InvalidException;
import com.example.shortform.exception.NotFoundException;
import com.example.shortform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final ChallengeRepository challengeRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final ChallengeService challengeService;

    @Transactional
    public void createChatRoom(ChatRoomRequestDto requestDto, PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        Challenge challenge = challengeRepository.findById(requestDto.getChallengeId()).orElseThrow(
                () -> new NotFoundException("챌린지가 존재하지 않습니다.")
        );
        if (challenge.getChatRoom() != null) {
            throw new InvalidException("이미 채팅방이 존재합니다.");
        }
        ChatRoom chatRoom = requestDto.toEntity(user.getProfileImage());
        chatRoomRepository.save(chatRoom);
        challenge.setChatRoom(chatRoom);
        UserChatRoom userChatRoom = requestDto.toEntity(chatRoom, user);
        userChatRoomRepository.save(userChatRoom);
    }

    @Transactional
    public List<ChatRoomListResponseDto> getAllMyRooms(PrincipalDetails principalDetails) throws ParseException {
        User user = principalDetails.getUser();
        List<ChatRoomListResponseDto> chatRoomResponseDtoList = new ArrayList<>();
        List<UserChallenge> userChallenges = userChallengeRepository.findAllByUser(user);

        for (UserChallenge userChallenge : userChallenges) {
            List<String> profileImageList = new ArrayList<>();
            List<ChatRoomMemberDto> memberList = new ArrayList<>();
            Challenge challenge = userChallenge.getChallenge();
            String status = challengeService.challengeStatus(challenge);
            if (status.equals("진행중")) {
                List<UserChallenge> userList = userChallengeRepository.findAllByChallenge(challenge);
                for (UserChallenge userC : userList) {
                    User member = userC.getUser();
                    memberList.add(member.toChatMemberResponse());
                    profileImageList.add(member.getProfileImage());
                }
                ChatRoom chatRoom = challenge.getChatRoom();
                int roomCnt = userChatRoomRepository.findUserCnt(chatRoom);
                ChatMessage chatMessage = Optional.ofNullable(chatMessageRepository.findFirstByChatRoomIdOrderByCreatedAtDesc(chatRoom.getId()))
                        .orElse(ChatMessage.builder()
                                .content("")
                                .build());

                String recentMessage = chatMessage.getContent();

                ChatRoomListResponseDto chatRoomResponseDto = chatRoom.toResponseList(
                            chatRoom.getCreatedAt(),
                            profileImageList,
                            roomCnt,
                            memberList,
                            recentMessage
                    );
                chatRoomResponseDtoList.add(chatRoomResponseDto);
            }

        }
        return chatRoomResponseDtoList;
    }

    @Transactional
    public ChatMessageListDto getAllMessages(Long roomId, PrincipalDetails principalDetails, Pageable pageable) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new NotFoundException("채팅방이 존재하지 않습니다.")
        );

        User user = principalDetails.getUser();

        Page<ChatMessage> messagePage = chatMessageRepository.findAllChatRoomMessage(chatRoom, pageable);
        List<UserChatRoom> memberList = userChatRoomRepository.findAllChatRoomUser(chatRoom);
        List<ChatMessageResponseDto> responseDtoList = new ArrayList<>();

        for (ChatMessage chatMessage : messagePage) {
            String createdAt = chatMessage.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
            ChatMessageResponseDto responseDto = chatMessage.toResponse(createdAt);
            responseDtoList.add(responseDto);
        }
        responseDtoList = responseDtoList.stream()
                .sorted(Comparator.comparing(ChatMessageResponseDto::getCreatedAt))
                .collect(Collectors.toList());

        ChatMessageListDto chatMessageList = ChatMessageListDto.builder()
                .roomName(chatRoom.getChallenge().getTitle())
                .messageList(responseDtoList)
                .currentMember(memberList.size())
                .roomId(roomId)
                .next(messagePage.hasNext())
                .build();

        return chatMessageList;
    }

}
