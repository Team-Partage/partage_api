package com.egatrap.partage.controller;

import com.egatrap.partage.common.aspect.MessagePermission;
import com.egatrap.partage.constants.MessageType;
import com.egatrap.partage.model.dto.ChannelCacheDataDto;
import com.egatrap.partage.model.dto.chat.ChatMessageDto;
import com.egatrap.partage.model.dto.chat.MessageDto;
import com.egatrap.partage.model.dto.chat.RequestPlayVideoDto;
import com.egatrap.partage.model.dto.chat.SendMessageDto;
import com.egatrap.partage.model.vo.SessionAttributes;
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

    @MessageMapping("/channel.info")
    public void channelInfo(SimpMessageHeaderAccessor headerAccessor) {
        SessionAttributes session = new SessionAttributes(headerAccessor);
        log.debug("Channel info: {}", session.getChannelId());

//        ChannelCacheDataDto channelCacheData = channelService.getChannelCacheData(session.getChannelId());

        // Create channel info data
//        Map<String, Object> data = new HashMap<>();
//        data.put("channel", channelService.getChannel(session.getChannelId()));
//        data.put("playlists", playlistService.getPlaylists(session.getChannelId(), 1, 10));
//        data.put("currentUsers", channelCacheData.getUsers());
//        data.put("currentPlayTime", channelCacheData.getCurrentPlayTime());
//        data.put("isPlaying", channelCacheData.getIsPlaying());
//        data.put("userRole", channelPermissionService.getChannelRole(session.getChannelId(), session.getUserId()));
//
//        log.debug("[data]=[{}]", data);
//
//        // Send to user only : channel info
//        messagingTemplate.convertAndSendToUser(session.getUserId(),
//                CHANNEL_PREFIX + session.getChannelId(),
//                SendMessageDto.builder()
//                        .data(data)
//                        .type(MessageType.CHANNEL_INFO)
//                        .build());
    }

    @MessageMapping("/user.chat")
    @MessagePermission(permission = MessageType.USER_CHAT)
    public void sendMessage(SimpMessageHeaderAccessor headerAccessor, @Validated @Payload ChatMessageDto message) {
        UserSession user = channelUserService.getUserSession(headerAccessor);
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
        log.debug("user.join");
        SessionAttributes session = new SessionAttributes(headerAccessor);
        log.info("User {} joined channel {}", session.getUserId(), session.getChannelId());
        log.info("SessionId: {}", headerAccessor.getSessionId());

        Map<String, Object> data = new HashMap<>();
        data.put("sender", session.getUserId());
        data.put("content", message.getContent());

        messagingTemplate.convertAndSend(CHANNEL_PREFIX + session.getChannelId(), SendMessageDto.builder()
                .data(data)
                .type(MessageType.USER_JOIN)
                .build());
    }

    @MessageMapping("/user.leave")
    public void leaveUser(SimpMessageHeaderAccessor headerAccessor, @Payload MessageDto message) {
        SessionAttributes session = new SessionAttributes(headerAccessor);
        log.info("User {} left channel {}", message.getSender(), session.getChannelId());

        Map<String, Object> data = new HashMap<>();
        data.put("sender", message.getSender());
        data.put("content", message.getContent());

        messagingTemplate.convertAndSend(CHANNEL_PREFIX + session.getChannelId(), SendMessageDto.builder()
                .data(data)
                .type(MessageType.USER_JOIN)
                .build());
    }

    @MessageMapping("/video.play")
    @MessagePermission(permission = MessageType.VIDEO_PLAY)
    public void playVideo(SimpMessageHeaderAccessor headerAccessor, @Payload RequestPlayVideoDto params) {
        SessionAttributes session = new SessionAttributes(headerAccessor);

        int playTime = channelSessionService.updatePlayTime(
                session.getChannelId(),
                params.getPlayTime(),
                params.isPlaying());

        Map<String, Object> data = new HashMap<>();
        data.put("playlistId", params.getPalylistId());
        data.put("playTime", params.getPlayTime());
        data.put("isPlaying", params.isPlaying());

        messagingTemplate.convertAndSend(CHANNEL_PREFIX + session.getChannelId(), SendMessageDto.builder()
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
