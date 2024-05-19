package com.egatrap.partage.model.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "authEmail", timeToLive = 60*5)
public class AuthEmail {

    public AuthEmail(String email, String authNum) {
        this.email = email;
        this.authNum = authNum;
    }

    @Id
    private String email;
    private String authNum;
}