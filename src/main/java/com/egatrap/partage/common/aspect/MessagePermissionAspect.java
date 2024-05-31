package com.egatrap.partage.common.aspect;

import com.egatrap.partage.constants.ChannelRoleType;
import com.egatrap.partage.constants.MessageType;
import com.egatrap.partage.model.dto.ChannelPermissionDto;
import com.egatrap.partage.model.dto.chat.MessageDto;
import com.egatrap.partage.service.ChannelPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MessagePermissionAspect {

    private final ChannelPermissionService channelPermissionService;

    @Before("@annotation(messagePermission) && args(message,..)")
    public void checkPermission(JoinPoint joinPoint, MessagePermission messagePermission, MessageDto message) {
        String userId = message.getSender();
        String channelId = message.getChannelId();

        ChannelRoleType userChannelRole = channelPermissionService.getChannelRole(channelId, userId);
        ChannelPermissionDto channelPermission = channelPermissionService.getChannelPermission(channelId);

        // 채널에 특정 유저의 권한이 있는지 확인 후 없으면 예외 발생 시킴
        if (!hasPermission(userChannelRole, messagePermission.permision(), channelPermission)) {
            throw new SecurityException("No permission to perform this action");
        }
    }

    private boolean hasPermission(ChannelRoleType userRole,
                                  MessageType messageType,
                                  ChannelPermissionDto channelPermission) {
        return switch (messageType) {
            // Layer 1
            case USER_CHAT -> userRole.getROLE_PRIORITY() <= channelPermission.getChatSend().getROLE_PRIORITY();
            case USER_LEAVE, USER_JOIN -> true;

            // Layer 2
            case VIDEO_PLAY -> userRole.getROLE_PRIORITY() <= channelPermission.getVideoPlay().getROLE_PRIORITY();
            case VIDEO_SEEK -> userRole.getROLE_PRIORITY() <= channelPermission.getVideoSeek().getROLE_PRIORITY();
            case VIDEO_SKIP -> userRole.getROLE_PRIORITY() <= channelPermission.getVideoSkip().getROLE_PRIORITY();

            // Layer 3
            case PLAYLIST_ADD -> userRole.getROLE_PRIORITY() <= channelPermission.getPlaylistAdd().getROLE_PRIORITY();
            case PLAYLIST_REMOVE ->
                    userRole.getROLE_PRIORITY() <= channelPermission.getPlaylistRemove().getROLE_PRIORITY();
            case PLAYLIST_MOVE -> userRole.getROLE_PRIORITY() <= channelPermission.getPlaylistMove().getROLE_PRIORITY();

            // Layer 4
            case USER_BAN -> userRole.getROLE_PRIORITY() <= channelPermission.getBan().getROLE_PRIORITY();

            default -> false;
        };
    }
}
