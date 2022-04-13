package com.example.shortform.dto.resonse;

import com.example.shortform.domain.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeResponseDto {

    private boolean read;
    private MemberResponseDto userInfo;
    private String date;
    private Notice.NoticeType status;
    private Long challengeId;
    private String title;
    private int challengeCnt;
    private Long roomId;
    private int levelPoint;
}
