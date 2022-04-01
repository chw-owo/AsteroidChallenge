package com.example.shortform.service;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.Notice;
import com.example.shortform.domain.User;
import com.example.shortform.dto.ResponseDto.NoticeResponseDto;
import com.example.shortform.exception.NotFoundException;
import com.example.shortform.repository.ChallengeRepository;
import com.example.shortform.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final ChallengeRepository challengeRepository;

    public ResponseEntity<List<NoticeResponseDto>> getNoticeList(PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();

        LocalDateTime now = LocalDateTime.now();

        List<Notice> noticeList = noticeRepository.findAllByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(user.getId(), now.minusWeeks(1));
        List<NoticeResponseDto> noticeResponseDtoList = new ArrayList<>();

        for (Notice notice : noticeList) {
            if (!notice.getNoticeType().equals(Notice.NoticeType.RECORD)) {
                String noticeCreatedAt = notice.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
                NoticeResponseDto noticeResponseDto;
                if (notice.getChallengeId() != null){
                    Challenge challenge = challengeRepository.findById(notice.getChallengeId()).orElseThrow(
                            () -> new NotFoundException("존재하지 않는 챌린지입니다.")
                    );
                    noticeResponseDto = notice.toChallengeResponse(user.toMemberResponse(), noticeCreatedAt, challenge);
                } else
                    noticeResponseDto = notice.toResponse(user.toMemberResponse(), noticeCreatedAt);
                if (!notice.is_read()) {
                    notice.setIs_read(true);
                    noticeRepository.save(notice);
                }
                noticeResponseDtoList.add(noticeResponseDto);
            }
        }

        return ResponseEntity.ok(noticeResponseDtoList);
    }
}
