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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
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
        // 채팅방과 챌린지가 oneToOne이므로 이미 채팅방 존재시 생성 불가
        if (challenge.getChatRoom() != null) {
            throw new InvalidException("이미 채팅방이 존재합니다.");
        }
        // 채팅방 DB에 저장
        ChatRoom chatRoom = requestDto.toEntity(user.getProfileImage());
        chatRoomRepository.save(chatRoom);
        //챌린지에 채팅방 FK
        challenge.setChatRoom(chatRoom);
        // 채팅방 명단에 채팅방 생성한 유저 등록
        UserChatRoom userChatRoom = requestDto.toEntity(chatRoom, user);
        userChatRoomRepository.save(userChatRoom);
    }

    @Transactional
    public List<ChatRoomListResponseDto> getAllMyRooms(PrincipalDetails principalDetails) throws ParseException {
        // 인증 정보를 이용해 유저 정보 획득
        User user = principalDetails.getUser();
        List<ChatRoomListResponseDto> chatRoomResponseDtoList = new ArrayList<>();
        // 유저가 참가한 챌린지 목록 조회
        List<UserChallenge> userChallenges = userChallengeRepository.findAllByUser(user);

        for (UserChallenge userChallenge : userChallenges) {
            List<String> profileImageList = new ArrayList<>();
            List<ChatRoomMemberDto> memberList = new ArrayList<>();
            Challenge challenge = userChallenge.getChallenge();
            String status = challengeService.challengeStatus(challenge);
            // 진행중인 챌린지만 채팅방이 존재하므로 status 사용
            if (status.equals("진행중")) {
                // 챌린지에 참여 중인 유저 목록 조회
                List<UserChallenge> userList = userChallengeRepository.findAllByChallenge(challenge);
                for (UserChallenge userC : userList) {
                    User member = userC.getUser();
                    memberList.add(member.toChatMemberResponse());
                    profileImageList.add(member.getProfileImage());
                }
                ChatRoom chatRoom = challenge.getChatRoom();
                // 채팅방에 참여 중인 유저 목록 조회
                List<UserChatRoom> userChatRooms = userChatRoomRepository.findAllByChatRoom(chatRoom);
                // 채팅방의 채팀 리스트 조회
                List<ChatMessage> chatMessageList = chatMessageRepository.findAllByChatRoom(chatRoom);

                String recentMessage;
                // 채팅 리스트가 없으면 nullpointexception 발생하므로 처리
                // 리스트 존재시 최근 채팅 추출
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
    public ChatMessageListDto getAllMessages(Long roomId, PrincipalDetails principalDetails, PageRequest pageRequest) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new NotFoundException("채팅방이 존재하지 않습니다.")
        );

        User user = principalDetails.getUser();

        // 채팅 메세지 DB에서 이 채팅방의 메세지 전체 조회
        Page<ChatMessage> messagePage = chatMessageRepository.findAllByChatRoom(chatRoom, pageRequest);
//        List<ChatMessage> messageList = chatMessageRepository.findAllByChatRoom(chatRoom);
        // 채팅 방 참가자 목록 조회
        List<UserChatRoom> memberList = userChatRoomRepository.findAllByChatRoom(chatRoom);

        List<ChatMessageResponseDto> responseDtoList = new ArrayList<>();

        //메세지 날짜 형식 변경
        for (ChatMessage chatMessage : messagePage) {
//            String createdAt = chatMessage.getCreatedAt().toString();
//            String year = createdAt.substring(0,4) + ".";
//            String month = createdAt.substring(5,7) + ".";
//            String day = createdAt.substring(8,10) + " ";
//            String time = createdAt.substring(11,16);
//            createdAt = year + month + day + time;
            String createdAt = chatMessage.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
            ChatMessageResponseDto responseDto = chatMessage.toResponse(createdAt);
            responseDtoList.add(responseDto);
        }

        // responseDto로 변경 후 return
        ChatMessageListDto chatMessageList = ChatMessageListDto.builder()
                .roomName(chatRoom.getChallenge().getTitle())
                .messageList(responseDtoList)
                .currentMember(memberList.size())
                .roomId(roomId)
                .next(messagePage.hasNext())
                .build();

        return chatMessageList;
    }

    @Transactional
    public void deleteRoom(Long roomId, PrincipalDetails principalDetails) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new NotFoundException("채팅방이 존재하지 않습니다.")
        );
        chatRoom.getChallenge().setChatRoom(null);
//        chatRoomRepository.deleteById(roomId);
    }
}
