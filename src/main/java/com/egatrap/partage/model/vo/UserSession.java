package com.egatrap.partage.model.vo;

import com.egatrap.partage.constants.ChannelRoleType;
import com.egatrap.partage.model.entity.ChannelUserEntity;
import lombok.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class UserSession {

    private String id;
    private String userId;
    private String channelId;
    private String nickname;
    private ChannelRoleType role;
    private LocalDateTime lastAccessTime;
    private LocalDateTime createdAt;

    public UserSession(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        UserSession userSession = (UserSession) sessionAttributes.get("userSession");
        this.id = userSession.getId();
        this.userId = userSession.getUserId();
        this.channelId = userSession.getChannelId();
        this.nickname = userSession.getNickname();
        this.role = userSession.getRole();
        this.lastAccessTime = userSession.getLastAccessTime();
        this.createdAt = userSession.getCreatedAt();
    }

    public UserSession(ChannelUserEntity user) {
        this.id = user.getSessionId();
        this.userId = user.getId().getUserId();
        this.channelId = user.getId().getChannelId();
        this.nickname = user.getUser().getNickname();
        this.role = ChannelRoleType.valueOf(user.getRole().getRoleName());
        this.lastAccessTime = user.getLastAccessAt();
        this.createdAt = user.getCreateAt();
    }

}
