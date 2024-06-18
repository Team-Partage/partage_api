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
@RedisHash(value = "user-session", timeToLive = 36000) // 10 hours
public class UserSessionEntity {

    @Id
    private String id; // session id

    @Indexed
    private String userId;

    @Indexed
    private String channelId;

    private String nickname;

    private ChannelRoleType channelRole;
    private LocalDateTime joinTime;
    private LocalDateTime lastAccessTime;


    public void updateLastAccessTime() {
        this.lastAccessTime = LocalDateTime.now();
    }

}
