package com.example.shortform.dto.ResponseDto;

import com.example.shortform.domain.Notice;
import com.example.shortform.dto.resonse.MemberResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeResponseDto {

    private boolean is_read;
    private MemberResponseDto userInfo;
    private String date;
    private Notice.NoticeType status;
    private Long challengeId;
    private String title;
    private int challengeCnt;
    private Long postId;
    private int levelPoint;
}
