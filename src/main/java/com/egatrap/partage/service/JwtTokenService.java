package com.egatrap.partage.service;

import com.egatrap.partage.model.dto.JwtTokenMappingDto;
import com.egatrap.partage.model.dto.ResponseLoginDto;
import com.egatrap.partage.model.entity.JwtTokenMappingEntity;
import com.egatrap.partage.repository.JwtTokenMappingRepository;
import com.egatrap.partage.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service("jwtTokenService")
@RequiredArgsConstructor
public class JwtTokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${security.jwt.refresh-token-expire}")
    private long REFRESH_TOKEN_EXPIRATION;

    /**
     * 토큰을 생성하고 Redis에 저장합니다.
     *
     * @param authentication 인증 객체 (Spring Security) - 로그인 성공 시 생성되는 객체 (Authentication)
     * @return JwtTokenMappingDto - 생성된 토큰 정보를 담은 DTO 객체 (accessToken, refreshToken)
     */
    public JwtTokenMappingDto generateToken(Authentication authentication) {

        // Generate token
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        log.debug("[accessToken]=[{}]", accessToken);
        log.debug("[refreshToken]=[{}]", refreshToken);

        // Create token entity
//        JwtTokenMappingEntity tokenEntity = JwtTokenMappingEntity.builder()
//                .id(refreshToken)
//                .accessToken(accessToken)
//                .build();

        // @TODO: 저장 방식 변경
        // Save token to Redis with expiration time
        redisTemplate.opsForValue().set(refreshToken, accessToken, REFRESH_TOKEN_EXPIRATION, TimeUnit.MICROSECONDS);

        // Return token
        return JwtTokenMappingDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
