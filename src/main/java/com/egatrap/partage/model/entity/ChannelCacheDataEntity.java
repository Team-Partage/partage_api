package com.egatrap.partage.model.entity;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@RedisHash(value = "channel-cache-data")
public class ChannelCacheDataEntity {

    @Id
    private String id; // channelId

    @Indexed
    private List<UserSessionEntity> users;

    private Integer currentPlayTime;
    private Boolean isPlaying;

    private LocalDateTime lastUpdateAt;


    public void updateLastUpdateAt() {
        this.lastUpdateAt = LocalDateTime.now();
    }

}
