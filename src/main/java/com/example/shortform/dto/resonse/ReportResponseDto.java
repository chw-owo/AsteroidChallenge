package com.example.shortform.dto.resonse;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReportResponseDto {

    private String date;
    private int percentage;

}
