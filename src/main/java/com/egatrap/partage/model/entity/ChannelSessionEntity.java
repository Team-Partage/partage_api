package com.egatrap.partage.model.entity;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@RedisHash(value = "channel-session")
public class ChannelSessionEntity {

    @Id
    private String id; // channelId

    private LocalDateTime lastActiveTime;

    public void updateLastActiveTime() {
        this.lastActiveTime = LocalDateTime.now();
    }
}
