package com.egatrap.partage.model.vo;

import com.egatrap.partage.constants.ChannelRoleType;
import lombok.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.util.Map;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class SessionAttributes {

    private String userId;
    private String channelId;
    private String sessionId;
    private ChannelRoleType channelRole;

    public SessionAttributes(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes != null) {
            this.sessionId = sessionAttributes.get("sessionId") == null ? null : String.valueOf(sessionAttributes.get("sessionId"));
            this.userId = sessionAttributes.get("userId") == null ? null : String.valueOf(sessionAttributes.get("userId"));
            this.channelId = String.valueOf(Objects.requireNonNull(sessionAttributes).get("channelId"));
            this.channelRole = sessionAttributes.get("channelRole") == null
                    ? ChannelRoleType.ROLE_NONE
                    : ChannelRoleType.valueOf(String.valueOf(sessionAttributes.get("channelRole")));
        } else {
            throw new IllegalArgumentException("Session attributes is null");
        }
    }
}
