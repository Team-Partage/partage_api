package com.egatrap.partage.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "cached-chat")
public class CachedChatEntity {

    private Long id;
    private String userId;
    private String channelId;
    private String message;
    private String createdAt;

}