package com.egatrap.partage.controller;

import com.egatrap.partage.common.aspect.MessagePermission;
import com.egatrap.partage.constants.MessageType;
import com.egatrap.partage.model.dto.chat.MessageDto;
import com.egatrap.partage.model.dto.chat.SendMessageDto;
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
public class StompController {

    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/test/ws")
    public String wsPage() {
        return "/ws.html";
    }

    @MessageMapping("/user.chat")
    @MessagePermission(permision = MessageType.USER_CHAT)
    public void sendMessage(SimpMessageHeaderAccessor headerAccessor, @Payload MessageDto message) {
        log.debug("Sending message to channel {}: {}", message.getChannelId(), message.getContent());
//        log.debug("User: {}", headerAccessor.getUser().getName());
        log.debug("SessionId: {}", headerAccessor.getSessionId());
        log.debug("SessionAttributes: {}", headerAccessor.getSessionAttributes());
        messagingTemplate.convertAndSend("/topic/" + message.getChannelId(), SendMessageDto.builder()
                .content(message.getContent())
                .sender(message.getSender())
                .type(MessageType.USER_CHAT)
                .build());
    }

    @MessageMapping("/user.join")
    @MessagePermission(permision = MessageType.USER_JOIN)
    public void addUser(@Payload MessageDto message) {
        log.info("User {} joined channel {}", message.getSender(), message.getChannelId());
        messagingTemplate.convertAndSend("/topic/" + message.getChannelId(), SendMessageDto.builder()
                .content(message.getContent())
                .sender(message.getSender())
                .type(MessageType.USER_JOIN)
                .build());
    }

    @MessageMapping("/user.leave")
    @MessagePermission(permision = MessageType.USER_LEAVE)
    public void leaveUser(@Payload MessageDto message) {
        log.info("User {} left channel {}", message.getSender(), message.getChannelId());
        messagingTemplate.convertAndSend("/topic/" + message.getChannelId(), SendMessageDto.builder()
                .content(message.getContent())
                .sender(message.getSender())
                .type(MessageType.USER_LEAVE)
                .build());
    }

}
