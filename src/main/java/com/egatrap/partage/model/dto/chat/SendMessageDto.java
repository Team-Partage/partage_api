package com.egatrap.partage.model.dto.chat;

import com.egatrap.partage.constants.MessageType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendMessageDto {

    private String sender;
    private String content;
    private MessageType type;

}
