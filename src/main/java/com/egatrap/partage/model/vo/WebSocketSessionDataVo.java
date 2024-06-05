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
public class WebSocketSessionDataVo {

    private String userId;
    private String channelId;
    private ChannelRoleType channelRole;

    public WebSocketSessionDataVo(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        this.userId = sessionAttributes.get("userId") == null ? null : String.valueOf(Objects.requireNonNull(sessionAttributes).get("userId"));
        this.channelId = String.valueOf(Objects.requireNonNull(sessionAttributes).get("channelId"));
        this.channelRole = ChannelRoleType.valueOf(String.valueOf(Objects.requireNonNull(sessionAttributes).get("channelRole")));
    }

}
