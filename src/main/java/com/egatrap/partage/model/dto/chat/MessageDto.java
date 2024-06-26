package com.egatrap.partage.model.dto.chat;

import com.egatrap.partage.constants.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

    private String content;
    private String sender;
    private String targetUserId;

}
