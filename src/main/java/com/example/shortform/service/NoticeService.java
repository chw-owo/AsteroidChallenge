package com.example.shortform.service;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.domain.Notice;
import com.example.shortform.domain.User;
import com.example.shortform.dto.ResponseDto.NoticeResponseDto;
import com.example.shortform.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public ResponseEntity<List<NoticeResponseDto>> getNoticeList(PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();

        List<Notice> noticeList = noticeRepository.findAllByUserId(user.getId());
        List<NoticeResponseDto> noticeResponseDtoList = new ArrayList<>();

        for (Notice notice : noticeList) {
            if (!notice.is_read())
                notice.setIs_read(true);
            String noticeCreatedAt = notice.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
            NoticeResponseDto noticeResponseDto = notice.toResponse(user.toMemberResponse(), noticeCreatedAt);
            noticeResponseDtoList.add(noticeResponseDto);
        }

        return ResponseEntity.ok(noticeResponseDtoList);
    }
}
