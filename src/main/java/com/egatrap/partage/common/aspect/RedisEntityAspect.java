package com.egatrap.partage.common.aspect;

import com.egatrap.partage.model.entity.ChannelCacheDataEntity;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Aspect
@Configuration
@RequiredArgsConstructor
public class RedisEntityAspect {

    private final RedisTemplate<String, Object> redisTemplate;


    @Before("execution(* org.springframework.data.redis.core.RedisTemplate.save(..)) && args(entity)")
    public void beforeSave(Object entity) {
        if (entity instanceof ChannelCacheDataEntity) {
            // 레디스 엔티티 저장 시 lastUpdateAt 업데이트
            ((ChannelCacheDataEntity) entity).updateLastUpdateAt();
        }
    }

}
