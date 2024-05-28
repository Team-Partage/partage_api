package com.egatrap.partage.controller;

import com.egatrap.partage.model.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    @MessageMapping("/chat/{channelNo}")
    @SendTo("/topic/{channelNo}")
    public ChatMessageDto sendMessage(ChatMessageDto message) {
        log.info("message: {}", message.getMessage());
        return message;
    }

}
