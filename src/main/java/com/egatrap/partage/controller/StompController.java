package com.egatrap.partage.controller;

import com.egatrap.partage.common.aspect.MessagePermission;
import com.egatrap.partage.constants.MessageType;
import com.egatrap.partage.model.dto.chat.MessageDto;
import com.egatrap.partage.model.dto.chat.SendMessageDto;
import com.egatrap.partage.model.vo.WebSocketSessionDataVo;
import com.egatrap.partage.service.ChannelService;
import com.egatrap.partage.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StompController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChannelService channelService;
    private final PlaylistService playlistService;

    private final String CHANNEL_PREFIX = "/channel/";

    @GetMapping("/test/ws")
    public String wsPage() {
        return "/ws.html";
    }

    @MessageMapping("/channel.info")
    public void channelInfo(SimpMessageHeaderAccessor headerAccessor) {
        WebSocketSessionDataVo session = new WebSocketSessionDataVo(headerAccessor);
        log.debug("Channel info: {}", session.getChannelId());

        // Create channel info data
        Map<String, Object> data = new HashMap<>();
        // @todo : 서비스 로직 분리 해야함
        data.put("channel", channelService.getChannel(session.getChannelId()));
        data.put("playlists", 0);
        data.put("currentPlaylist", 0);
        data.put("currentUsers", 0);
        data.put("userPermission", 0);

        // Send to user only : channel info
        messagingTemplate.convertAndSendToUser(session.getSessionId(),
                CHANNEL_PREFIX + session.getChannelId(),
                SendMessageDto.builder()
                        .data(data)
                        .type(MessageType.CHANNEL_INFO)
                        .build());
    }

    @MessageMapping("/user.chat")
    @MessagePermission(permission = MessageType.USER_CHAT)
    public void sendMessage(SimpMessageHeaderAccessor headerAccessor, @Payload MessageDto message) {
        WebSocketSessionDataVo session = new WebSocketSessionDataVo(headerAccessor);
        log.debug("Sending message to channel {}: {}", session.getChannelId(), message.getContent());
        log.debug("SessionId: {}", headerAccessor.getSessionId());
        log.debug("session: {}", session);

        Map<String, Object> data = new HashMap<>();
        data.put("sender", message.getSender());
        data.put("content", message.getContent());

        messagingTemplate.convertAndSend(CHANNEL_PREFIX + session.getChannelId(), SendMessageDto.builder()
                .data(data)
                .type(MessageType.USER_JOIN)
                .build());
    }

    @MessageMapping("/user.join")
    public void addUser(SimpMessageHeaderAccessor headerAccessor, @Payload MessageDto message) {
        log.debug("user.join");
        WebSocketSessionDataVo session = new WebSocketSessionDataVo(headerAccessor);
        log.info("User {} joined channel {}", message.getSender(), session.getChannelId());

        Map<String, Object> data = new HashMap<>();
        data.put("sender", message.getSender());
        data.put("content", message.getContent());

        messagingTemplate.convertAndSend(CHANNEL_PREFIX + session.getChannelId(), SendMessageDto.builder()
                .data(data)
                .type(MessageType.USER_JOIN)
                .build());
    }

    @MessageMapping("/user.leave")
    public void leaveUser(SimpMessageHeaderAccessor headerAccessor, @Payload MessageDto message) {
        WebSocketSessionDataVo session = new WebSocketSessionDataVo(headerAccessor);
        log.info("User {} left channel {}", message.getSender(), session.getChannelId());

        Map<String, Object> data = new HashMap<>();
        data.put("sender", message.getSender());
        data.put("content", message.getContent());

        messagingTemplate.convertAndSend(CHANNEL_PREFIX + session.getChannelId(), SendMessageDto.builder()
                .data(data)
                .type(MessageType.USER_JOIN)
                .build());
    }

}
