package com.egatrap.partage.service;


import com.egatrap.partage.model.dto.ChannelSessionDto;
import com.egatrap.partage.model.entity.ChannelSessionEntity;
import com.egatrap.partage.model.entity.UserSessionEntity;
import com.egatrap.partage.model.vo.SessionAttributes;
import com.egatrap.partage.repository.ChannelSessionRepository;
import com.egatrap.partage.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@Service("channelUserService")
public class ChannelUserService {

    private final UserSessionRepository userSessionRepository;
    private final ChannelSessionRepository channelSessionRepository;
    private final ModelMapper modelMapper;

    /**
     * 채널에 유저 추가
     *
     * @param sessionData 세션 정보
     */
    public void addUsersToChannel(SessionAttributes sessionData) {
        addUserToChannel(sessionData.getSessionId(), sessionData.getUserId(), sessionData.getChannelId());
    }

    /**
     * 채널에 유저 세션 추가
     *
     * @param channelId 채널 아이디
     */
    public void addUserToChannel(String sessionId, String userId, String channelId) {
        Optional<ChannelSessionEntity> channelSession = channelSessionRepository.findById(channelId);

        UserSessionEntity userSession = UserSessionEntity.builder()
                .id(sessionId)
                .userId(userId)
                .channelId(channelId)
                .joinTime(LocalDateTime.now())
                .lastActiveTime(LocalDateTime.now())
                .build();

        // 채널이 없으면 생성
        if (channelSession.isEmpty()) {
            channelSessionRepository.save(ChannelSessionEntity.builder()
                    .id(channelId)
                    .lastActiveTime(LocalDateTime.now())
                    .build());
        }

        log.debug("Redis User added to channel: sessionId={}, userId={}, channelId={}", sessionId, userId, channelId);
        userSessionRepository.save(userSession);
    }

    /**
     * 채널에서 유저 제거
     *
     * @param sessionId 세션 아이디
     */
    public void removeUserFromChannel(String sessionId) {
        log.debug("Redis User removed from channel: sessionId={}", sessionId);
        userSessionRepository.deleteById(sessionId);
    }

    /**
     * 세션 아이디로 세션 정보 조회
     *
     * @param sessionId 세션 아이디
     * @return 세션 정보
     */
    public SessionAttributes getSessionAttributes(String sessionId) {
        UserSessionEntity channelUserEntity = userSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("UserSessionEntity not found: sessionId=" + sessionId));
        return SessionAttributes.builder()
                .sessionId(channelUserEntity.getId())
                .userId(channelUserEntity.getUserId())
//                .channelId(channelUserEntity.getChannelId())
                .channelRole(channelUserEntity.getChannelRole())
                .build();
    }

    // channel session 아이디 리스트 조회
    public List<ChannelSessionDto> getChannels() {
        Iterable<ChannelSessionEntity> channelSessionEntities = channelSessionRepository.findAll();
        List<ChannelSessionDto> result = new ArrayList<>();

        for (ChannelSessionEntity channelSessionEntity : channelSessionEntities) {
            result.add(modelMapper.map(channelSessionEntity, ChannelSessionDto.class));
        }

        return result;
    }

    public long countUserByChannel(String channelId) {
        Iterable<UserSessionEntity> users = userSessionRepository.findAllByChannelId(channelId);
        return StreamSupport.stream(users.spliterator(), false).count();
    }

    public long countUser() {
        return userSessionRepository.count();
    }

    public long countChannel() {
        return channelSessionRepository.count();
    }

}