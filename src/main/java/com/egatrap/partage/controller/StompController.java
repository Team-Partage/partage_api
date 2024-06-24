package com.egatrap.partage.controller;

import com.egatrap.partage.common.aspect.MessagePermission;
import com.egatrap.partage.constants.MessageType;
import com.egatrap.partage.model.dto.PlaylistDto;
import com.egatrap.partage.model.dto.chat.*;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StompController {

    private final SimpMessagingTemplate messagingTemplate;
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

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getUserId());
        data.put("nickname", message.getNickname());
        data.put("profile", message.getProfile());
        data.put("message", message.getMessage());
        data.put("sendTime", LocalDateTime.now());

        messagingTemplate.convertAndSend(CHANNEL_PREFIX + user.getChannelId(), SendMessageDto.builder()
                .data(data)
                .type(MessageType.USER_CHAT)
                .build());
    }

    @MessageMapping("/user.join")
    public void addUser(SimpMessageHeaderAccessor headerAccessor) {
        UserSession user = new UserSession(headerAccessor);

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getUserId());

        messagingTemplate.convertAndSend(CHANNEL_PREFIX + user.getChannelId(), SendMessageDto.builder()
                .data(data)
                .type(MessageType.USER_JOIN)
                .build());
    }

    @MessageMapping("/user.leave")
    public void leaveUser(SimpMessageHeaderAccessor headerAccessor) {
        UserSession user = new UserSession(headerAccessor);

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getUserId());

        messagingTemplate.convertAndSend(CHANNEL_PREFIX + user.getChannelId(), SendMessageDto.builder()
                .data(data)
                .type(MessageType.USER_LEAVE)
                .build());
    }

    @MessageMapping("/video.move")
    @MessagePermission(permission = MessageType.VIDEO_MOVE)
    public void moveVideo(SimpMessageHeaderAccessor headerAccessor, @Payload RequestMoveVideoDto params) {
        UserSession user = new UserSession(headerAccessor);

        int playtime = channelSessionService.updatePlayTime(user.getChannelId(), params.getPlaytime());

        Map<String, Object> data = new HashMap<>();
        data.put("playlistNo", params.getPlaylistNo());
        data.put("playtime", playtime);

        messagingTemplate.convertAndSend(CHANNEL_PREFIX + user.getChannelId(), SendMessageDto.builder()
                .data(data)
                .type(MessageType.VIDEO_PLAY)
                .build());
    }

    @MessageMapping("/video.play")
    @MessagePermission(permission = MessageType.VIDEO_PLAY)
    public void playVideo(SimpMessageHeaderAccessor headerAccessor, @Payload RequestPlayVideoDto params) {
        UserSession user = new UserSession(headerAccessor);

        channelSessionService.updatePlayStatus(user.getChannelId(), params.isPlaying());

        Map<String, Object> data = new HashMap<>();
        data.put("playlistNo", params.getPlaylistNo());
        data.put("playing", params.isPlaying());

        messagingTemplate.convertAndSend(CHANNEL_PREFIX + user.getChannelId(), SendMessageDto.builder()
                .data(data)
                .type(MessageType.VIDEO_PLAY)
                .build());
    }

    @MessageMapping("/playlist.add")
    @MessagePermission(permission = MessageType.PLAYLIST_ADD)
    public void addPlaylist(SimpMessageHeaderAccessor headerAccessor, @Payload RequestAddPlaylistDto params)
            throws Exception {
        UserSession user = new UserSession(headerAccessor);

        String videoId = null;
        String url = params.getUrl();

//        비디오 아이디 추출
        if (!url.startsWith("https://www.youtube.com/"))
            throw new IllegalArgumentException("Invalid URL : Not Youtube URL");

        // URL에서 비디오 아이디 추출 (유튜브)
        Pattern pattern = Pattern.compile(PlaylistController.VIDEO_ID_PATTERN);
        String[] parts = url.split("[/?=&]+");
        for (String part : parts) {
            Matcher matcher = pattern.matcher(part);
            if (matcher.matches()) {
                videoId = matcher.group();
                break;
            }
        }

        // 비디오 아이디가 없으면 URL이 잘못된 것으로 판단 후 에러 메시지 반환
        if (videoId == null)
            throw new IllegalArgumentException("Invalid URL : Not Found Video ID");
        log.debug("[videoId]=[{}]", videoId);

        // 플레이리스트 추가
        PlaylistDto data = playlistService.addPlaylist(user.getChannelId(), videoId);

        messagingTemplate.convertAndSend(CHANNEL_PREFIX + user.getChannelId(), SendMessageDto.builder()
                .data(data)
                .type(MessageType.PLAYLIST_ADD)
                .build());
    }

    @MessageMapping("/playlist.move")
    @MessagePermission(permission = MessageType.PLAYLIST_ADD)
    public void movePlaylist(SimpMessageHeaderAccessor headerAccessor, @Payload RequestMovePlaylistDto params)
            throws Exception {
        UserSession user = new UserSession(headerAccessor);

        playlistService.movePlaylist(params.getPlaylistNo(), params.getSequence());

        Map<String, Object> data = new HashMap<>();
        data.put("playlistNo", params.getPlaylistNo());
        data.put("sequence", params.getSequence());

        messagingTemplate.convertAndSend(CHANNEL_PREFIX + user.getChannelId(), SendMessageDto.builder()
                .data(data)
                .type(MessageType.PLAYLIST_ADD)
                .build());
    }

    @MessageMapping("/playlist.remove")
    @MessagePermission(permission = MessageType.PLAYLIST_REMOVE)
    public void removePlaylist(SimpMessageHeaderAccessor headerAccessor, @Payload RequestRemovePlaylistDto params)
            throws Exception {
        UserSession user = new UserSession(headerAccessor);

        playlistService.deletePlaylist(params.getPlaylistNo());

        Map<String, Object> data = new HashMap<>();
        data.put("playlistNo", params.getPlaylistNo());

        messagingTemplate.convertAndSend(CHANNEL_PREFIX + user.getChannelId(), SendMessageDto.builder()
                .data(data)
                .type(MessageType.PLAYLIST_REMOVE)
                .build());
    }

    //    ###################################################################################
    //                                      TEST CODE
    //    ###################################################################################
    @PostMapping("/test/video.play")
    public ResponseEntity<?> testPlaVideo(@Validated @RequestBody RequestPlayVideoDto params) {
        log.debug("RequestPlayVideoDto: {}", params);

        String channelId = "C-1cd8996b9b3946b9b54e5c65c20916";

        channelSessionService.updatePlayStatus(channelId, params.isPlaying());

        Map<String, Object> data = new HashMap<>();
        data.put("playlistNo", params.getPlaylistNo());
        data.put("isPlaying", params.isPlaying());

        messagingTemplate.convertAndSend(CHANNEL_PREFIX + channelId, SendMessageDto.builder()
                .data(data)
                .type(MessageType.VIDEO_PLAY)
                .build());

        return ResponseEntity.ok().build();
    }

}
