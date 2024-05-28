package com.egatrap.partage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {

    private Long chattingNo;
    private Long userNo;
    private Long channelNo;
    private String message;
    private String createAt;

}
