package com.egatrap.partage.model.entity;

import com.egatrap.partage.model.dto.ChannelDto;
import com.egatrap.partage.model.dto.UserDto;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

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

    private ChannelDto channel;
    private List<UserDto> users;
    private Integer currentPlayTime;
    private Boolean isPlaying;

    private LocalDateTime lastUpdateAt;


    public void updateLastUpdateAt() {
        this.lastUpdateAt = LocalDateTime.now();
    }

}
