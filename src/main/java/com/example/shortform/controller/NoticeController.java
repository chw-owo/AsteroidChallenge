package com.example.shortform.controller;

import com.example.shortform.config.auth.PrincipalDetails;
import com.example.shortform.dto.resonse.NoticeResponseDto;
import com.example.shortform.exception.NotFoundException;
import com.example.shortform.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/notice")
    public ResponseEntity<List<NoticeResponseDto>> getNoticeList(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails != null)
            return noticeService.getNoticeList(principalDetails);
        else
            throw new NotFoundException("로그인 후 이용가능합니다.");
    }
}
