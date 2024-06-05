package com.egatrap.partage.controller;

import com.egatrap.partage.common.aspect.MessagePermission;
import com.egatrap.partage.constants.MessageType;
import com.egatrap.partage.model.dto.chat.MessageDto;
import com.egatrap.partage.model.dto.chat.SendMessageDto;
import com.egatrap.partage.model.vo.WebSocketSessionDataVo;
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

    private final String CHANNEL_PREFIX = "/channel/";

    @GetMapping("/test/ws")
    public String wsPage() {
        return "/ws.html";
    }

    @MessageMapping("/user.chat")
    @MessagePermission(permission = MessageType.USER_CHAT)
    public void sendMessage(SimpMessageHeaderAccessor headerAccessor, @Payload MessageDto message) {
        WebSocketSessionDataVo session = new WebSocketSessionDataVo(headerAccessor);
        log.debug("Sending message to channel {}: {}", session.getChannelId(), message.getContent());
        log.debug("SessionId: {}", headerAccessor.getSessionId());
        log.debug("session: {}", session);
        messagingTemplate.convertAndSend(CHANNEL_PREFIX + session.getChannelId(), SendMessageDto.builder()
                .content(message.getContent())
                .sender(message.getSender())
                .type(MessageType.USER_CHAT)
                .build());
    }

    @MessageMapping("/user.join")
    @MessagePermission(permission = MessageType.USER_JOIN)
    public void addUser(SimpMessageHeaderAccessor headerAccessor, @Payload MessageDto message) {
        WebSocketSessionDataVo session = new WebSocketSessionDataVo(headerAccessor);
        log.info("User {} joined channel {}", message.getSender(), session.getChannelId());
        messagingTemplate.convertAndSend(CHANNEL_PREFIX + session.getChannelId(), SendMessageDto.builder()
                .content(message.getContent())
                .sender(message.getSender())
                .type(MessageType.USER_JOIN)
                .build());
    }

    @MessageMapping("/user.leave")
    @MessagePermission(permission = MessageType.USER_LEAVE)
    public void leaveUser(SimpMessageHeaderAccessor headerAccessor, @Payload MessageDto message) {
        WebSocketSessionDataVo session = new WebSocketSessionDataVo(headerAccessor);
        log.info("User {} left channel {}", message.getSender(), session.getChannelId());
        messagingTemplate.convertAndSend(CHANNEL_PREFIX + session.getChannelId(), SendMessageDto.builder()
                .content(message.getContent())
                .sender(message.getSender())
                .type(MessageType.USER_LEAVE)
                .build());
    }

}
