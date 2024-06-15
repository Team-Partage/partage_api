package com.egatrap.partage.model.entity;

import com.egatrap.partage.constants.ChannelRoleType;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(value = "user-session")
public class UserSessionEntity {

    @Id
    private String id; // session id

    @Indexed
    private String userId;

    @Indexed
    private String channelId;

    private ChannelRoleType channelRole;
    private LocalDateTime joinTime;
    private LocalDateTime lastActiveTime;

}
