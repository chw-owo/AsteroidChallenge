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

import javax.transaction.Transactional;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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
                List<UserChatRoom> userChatRooms = userChatRoomRepository.findAllByChatRoom(chatRoom);
                List<ChatMessage> chatMessageList = chatMessageRepository.findAllByChatRoom(chatRoom);

                String recentMessage;
                if (chatMessageList.size() == 0)
                    recentMessage = null;
                else
                    recentMessage = chatMessageList.get(chatMessageList.size() - 1).getContent();

                ChatRoomListResponseDto chatRoomResponseDto = challenge.getChatRoom().toResponseList(
                            chatRoom.getCreatedAt(),
                            profileImageList,
                            userChatRooms.size(),
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

        Page<ChatMessage> messagePage = chatMessageRepository.findAllByChatRoom(chatRoom, pageable);
//        List<ChatMessage> messageList = chatMessageRepository.findAllByChatRoom(chatRoom);
        List<UserChatRoom> memberList = userChatRoomRepository.findAllByChatRoom(chatRoom);

        List<ChatMessageResponseDto> responseDtoList = new ArrayList<>();

        for (ChatMessage chatMessage : messagePage) {
            String createdAt = chatMessage.getCreatedAt().toString();
            String year = createdAt.substring(0,4) + ".";
            String month = createdAt.substring(5,7) + ".";
            String day = createdAt.substring(8,10) + " ";
            String time = createdAt.substring(11,16);
            createdAt = year + month + day + time;
            ChatMessageResponseDto responseDto = chatMessage.toResponse(createdAt);
            responseDtoList.add(responseDto);
        }

        ChatMessageListDto chatMessageList = ChatMessageListDto.builder()
                .roomName(chatRoom.getChallenge().getTitle())
                .messageList(responseDtoList)
                .currentMember(memberList.size())
                .roomId(roomId)
                .build();

        return chatMessageList;
    }

    @Transactional
    public void deleteRoom(Long roomId, PrincipalDetails principalDetails) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new NotFoundException("채팅방이 존재하지 않습니다.")
        );
        chatRoomRepository.deleteById(roomId);
    }
}
