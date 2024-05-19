package com.egatrap.partage.model.entity;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@RedisHash(value = "jwt-token-mapping", timeToLive = 86400000)
public class JwtTokenMappingEntity {

    @Id
    private String id; // refreshToken

    private String accessToken;

}
