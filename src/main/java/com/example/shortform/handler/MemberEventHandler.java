package com.example.shortform.handler;

import com.example.shortform.domain.Notice;
import com.example.shortform.domain.User;
import com.example.shortform.mail.EmailService;
import com.example.shortform.repository.NoticeRepository;
import com.example.shortform.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class MemberEventHandler {

    private final RankingService rankingService;
    private final NoticeRepository noticeRepository;

    @TransactionalEventListener
    public void memberSignUpEventListener(User savedUser) {
        rankingService.updateRank(savedUser);

        Notice notice = Notice.builder()
                .noticeType(Notice.NoticeType.SIGNIN)
                .is_read(false)
                .user(savedUser)
                .build();

        noticeRepository.save(notice);
    }


}
