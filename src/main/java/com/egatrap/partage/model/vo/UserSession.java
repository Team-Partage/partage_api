package com.egatrap.partage.model.vo;

import com.egatrap.partage.constants.ChannelRoleType;
import lombok.*;

import java.time.LocalDateTime;

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
    private LocalDateTime joinTime;
    private LocalDateTime lastAccessTime;

}
