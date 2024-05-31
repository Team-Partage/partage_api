package com.egatrap.partage.controller;

import com.egatrap.partage.constants.MessageType;
import com.egatrap.partage.model.dto.chat.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
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
    public void sendMessage(@Payload MessageDto message) {
        log.debug("Sending message to channel {}: {}", message.getChannelId(), message.getContent());
        message.setType(MessageType.USER_CHAT);
        messagingTemplate.convertAndSend("/topic/" + message.getChannelId(), message);
    }

    @MessageMapping("/user.join")
    public void addUser(@Payload MessageDto message) {
        log.info("User {} joined channel {}", message.getSender(), message.getChannelId());
        message.setType(MessageType.USER_JOIN);
        messagingTemplate.convertAndSend("/topic/" + message.getChannelId(), message);
    }

    @MessageMapping("/user.leave")
    public void leaveUser(@Payload MessageDto message) {
        log.info("User {} left channel {}", message.getSender(), message.getChannelId());
        message.setType(MessageType.USER_LEAVE);
        messagingTemplate.convertAndSend("/topic/" + message.getChannelId(), message);
    }


//    @MessageMapping("/chat.sendMessage")
//    public void sendMessage(@Payload MessageDto chatMessage) {
//        log.info("Sending message to channel {}: {}", chatMessage.getChannelId(), chatMessage.getContent());
//        messagingTemplate.convertAndSend("/topic/" + chatMessage.getChannelId(), chatMessage);
//    }

//    @MessageMapping("/chat.addUser")
//    public void addUser(@Payload MessageDto chatMessage, SimpMessageHeaderAccessor headerAccessor) {
//        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
//        log.info("User {} joined channel {}", chatMessage.getSender(), chatMessage.getChannelId());
//        chatMessage.setType(MessageDto.MessageType.JOIN);
//        messagingTemplate.convertAndSend("/topic/" + chatMessage.getChannelId(), chatMessage);
//    }

//    @MessageMapping("/chat.leaveUser")
//    public void leaveUser(@Payload MessageDto chatMessage, SimpMessageHeaderAccessor headerAccessor) {
//        log.info("User {} left channel {}", chatMessage.getSender(), chatMessage.getChannelId());
//        chatMessage.setType(MessageDto.MessageType.LEAVE);
//        messagingTemplate.convertAndSend("/topic/" + chatMessage.getChannelId(), chatMessage);
//    }

}
