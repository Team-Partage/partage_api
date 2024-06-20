package com.egatrap.partage.controller;

import com.egatrap.partage.common.aspect.MessagePermission;
import com.egatrap.partage.constants.MessageType;
import com.egatrap.partage.model.dto.chat.ChatMessageDto;
import com.egatrap.partage.model.dto.chat.MessageDto;
import com.egatrap.partage.model.dto.chat.RequestPlayVideoDto;
import com.egatrap.partage.model.dto.chat.SendMessageDto;
import com.egatrap.partage.model.vo.UserSession;
import com.egatrap.partage.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StompController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChannelPermissionService channelPermissionService;
    private final ChannelService channelService;
    private final ChannelUserService channelUserService;
    private final ChannelSessionService channelSessionService;
    private final PlaylistService playlistService;

    public static final String CHANNEL_PREFIX = "/channel/";

    @GetMapping("/test/ws")
    public String wsPage() {
        return "/ws.html";
    }

    @MessageMapping("/user.chat")
    @MessagePermission(permission = MessageType.USER_CHAT)
    public void sendMessage(SimpMessageHeaderAccessor headerAccessor, @Validated @Payload ChatMessageDto message) {
        UserSession user = new UserSession(headerAccessor);
        log.debug("Sending message to channel {}: {}", user.getChannelId(), message.getMessage());

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getUserId());
        data.put("nickname", user.getNickname());
        data.put("message", message.getMessage());

        messagingTemplate.convertAndSend(CHANNEL_PREFIX + user.getChannelId(), SendMessageDto.builder()
                .data(data)
                .type(MessageType.USER_CHAT)
                .build());
    }

    @MessageMapping("/user.join")
    public void addUser(SimpMessageHeaderAccessor headerAccessor, @Payload MessageDto message) {
        UserSession user = new UserSession(headerAccessor);
        log.info("User {} joined channel {}", user.getUserId(), user.getChannelId());
        log.info("SessionId: {}", headerAccessor.getSessionId());

        Map<String, Object> data = new HashMap<>();
        data.put("sender", user.getUserId());
        data.put("content", message.getContent());

        messagingTemplate.convertAndSend(CHANNEL_PREFIX + user.getChannelId(), SendMessageDto.builder()
                .data(data)
                .type(MessageType.USER_JOIN)
                .build());
    }

    @MessageMapping("/user.leave")
    public void leaveUser(SimpMessageHeaderAccessor headerAccessor, @Payload MessageDto message) {
        UserSession user = new UserSession(headerAccessor);
        log.info("User {} left channel {}", message.getSender(), user.getChannelId());

        Map<String, Object> data = new HashMap<>();
        data.put("sender", message.getSender());
        data.put("content", message.getContent());

        messagingTemplate.convertAndSend(CHANNEL_PREFIX + user.getChannelId(), SendMessageDto.builder()
                .data(data)
                .type(MessageType.USER_JOIN)
                .build());
    }

    @MessageMapping("/video.play")
    @MessagePermission(permission = MessageType.VIDEO_PLAY)
    public void playVideo(SimpMessageHeaderAccessor headerAccessor, @Payload RequestPlayVideoDto params) {
        UserSession user = new UserSession(headerAccessor);

        int playTime = channelSessionService.updatePlayTime(
                user.getChannelId(),
                params.getPlayTime(),
                params.isPlaying());

        Map<String, Object> data = new HashMap<>();
        data.put("playlistId", params.getPalylistId());
        data.put("playTime", playTime);
        data.put("isPlaying", params.isPlaying());

        messagingTemplate.convertAndSend(CHANNEL_PREFIX + user.getChannelId(), SendMessageDto.builder()
                .data(data)
                .type(MessageType.VIDEO_PLAY)
                .build());
    }

    //    ###################################################################################
    //                                      TEST CODE
    //    ###################################################################################
    @PostMapping("/test/video.play")
    public ResponseEntity<?> testPlaVideo(@Validated @RequestBody RequestPlayVideoDto params) {
        log.debug("RequestPlayVideoDto: {}", params);

        String channelId = "C-3e270b0a697e45d8b083414968437e";

        int playTime = channelSessionService.updatePlayTime(channelId, params.getPlayTime(), params.isPlaying());

        Map<String, Object> data = new HashMap<>();
        data.put("playlistId", params.getPalylistId());
        data.put("playTime", playTime);
        data.put("isPlaying", params.isPlaying());

        messagingTemplate.convertAndSend(CHANNEL_PREFIX + channelId, SendMessageDto.builder()
                .data(data)
                .type(MessageType.VIDEO_PLAY)
                .build());

        return ResponseEntity.ok().build();
    }

}
