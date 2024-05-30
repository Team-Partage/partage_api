package com.egatrap.partage.controller;

import com.egatrap.partage.model.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/test/ws")
    public String wsPage() {
        return "/ws.html";
    }


    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDto chatMessage) {
        log.info("Sending message to channel {}: {}", chatMessage.getChannelId(), chatMessage.getContent());
        messagingTemplate.convertAndSend("/topic/" + chatMessage.getChannelId(), chatMessage);
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessageDto chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        log.info("User {} joined channel {}", chatMessage.getSender(), chatMessage.getChannelId());
        chatMessage.setType(ChatMessageDto.MessageType.JOIN);
        messagingTemplate.convertAndSend("/topic/" + chatMessage.getChannelId(), chatMessage);
    }

    @MessageMapping("/chat.leaveUser")
    public void leaveUser(@Payload ChatMessageDto chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        log.info("User {} left channel {}", chatMessage.getSender(), chatMessage.getChannelId());
        chatMessage.setType(ChatMessageDto.MessageType.LEAVE);
        messagingTemplate.convertAndSend("/topic/" + chatMessage.getChannelId(), chatMessage);
    }

}
