package com.egatrap.partage.service;


import com.egatrap.partage.model.dto.ChannelSessionDto;
import com.egatrap.partage.model.entity.ChannelSessionEntity;
import com.egatrap.partage.model.entity.UserEntity;
import com.egatrap.partage.model.entity.UserSessionEntity;
import com.egatrap.partage.model.vo.SessionAttributes;
import com.egatrap.partage.model.vo.UserSession;
import com.egatrap.partage.repository.ChannelSessionRepository;
import com.egatrap.partage.repository.UserRepository;
import com.egatrap.partage.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@Service("channelUserService")
public class ChannelUserService {

    private final UserSessionRepository userSessionRepository;
    private final ChannelSessionRepository channelSessionRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    public UserSession addUserSession(SimpMessageHeaderAccessor headerAccessor) {
        SessionAttributes session = new SessionAttributes(headerAccessor);
        return addUserSession(session.getSessionId(), session.getUserId(), session.getChannelId());
    }

    public UserSession addUserSession(SessionAttributes sessionData) {
        return addUserSession(sessionData.getSessionId(), sessionData.getUserId(), sessionData.getChannelId());
    }

    public UserSession addUserSession(String sessionId, String userId, String channelId) {
        Optional<ChannelSessionEntity> channelSession = channelSessionRepository.findById(channelId);

        UserEntity user = userRepository.findById(userId).orElse(null);

        UserSessionEntity userSession = UserSessionEntity.builder()
                .id(sessionId)
                .userId(userId)
                .nickname(user == null ? "NONE" : user.getNickname())
                .channelId(channelId)
                .joinTime(LocalDateTime.now())
                .lastAccessTime(LocalDateTime.now())
                .build();

        // 채널이 없으면 생성
        if (channelSession.isEmpty()) {
            channelSessionRepository.save(ChannelSessionEntity.builder()
                    .id(channelId)
                    .lastAccessTime(LocalDateTime.now())
                    .build());
        }

        log.debug("Redis User added to channel: sessionId={}, userId={}, channelId={}", sessionId, userId, channelId);
        userSessionRepository.save(userSession);

        return UserSession.builder()
                .id(userSession.getId())
                .userId(userSession.getUserId())
                .channelId(userSession.getChannelId())
                .nickname(userSession.getNickname())
                .role(userSession.getChannelRole())
                .lastAccessTime(userSession.getLastAccessTime())
                .joinTime(userSession.getJoinTime())
                .build();
    }

    public void removeUserSession(String sessionId) {
        log.debug("Redis User removed from channel: sessionId={}", sessionId);
        userSessionRepository.deleteById(sessionId);
    }

    public SessionAttributes getSessionAttributes(String sessionId) {
        UserSessionEntity channelUserEntity = userSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("UserSessionEntity not found: sessionId=" + sessionId));
        return SessionAttributes.builder()
                .sessionId(channelUserEntity.getId())
                .userId(channelUserEntity.getUserId())
                .channelId(channelUserEntity.getChannelId())
                .channelRole(channelUserEntity.getChannelRole())
                .build();
    }

    public UserSession getUserSession(SimpMessageHeaderAccessor headerAccessor) {
        SessionAttributes session = new SessionAttributes(headerAccessor);
        return getUserSession(session.getSessionId(), session.getChannelId(), session.getUserId());
    }

    public UserSession getUserSession(String sessionId, String channelId, String userId) {
        UserSessionEntity userSessionEntity = userSessionRepository.findById(sessionId).orElse(null);

        if (userSessionEntity == null) {
            return addUserSession(sessionId, userId, channelId);
        }

        userSessionEntity.updateLastAccessTime();
        log.debug("Redis User updated last access time: sessionId={}", sessionId);
        userSessionRepository.save(userSessionEntity);

        return UserSession.builder()
                .id(userSessionEntity.getId())
                .userId(userSessionEntity.getUserId())
                .channelId(userSessionEntity.getChannelId())
                .nickname(userSessionEntity.getNickname())
                .role(userSessionEntity.getChannelRole())
                .lastAccessTime(userSessionEntity.getLastAccessTime())
                .joinTime(userSessionEntity.getJoinTime())
                .build();
    }

    public void updateLastAccessTime(String sessionId) {
        UserSessionEntity userSessionEntity = userSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("UserSessionEntity not found: sessionId=" + sessionId));
        userSessionEntity.setLastAccessTime(LocalDateTime.now());
        userSessionRepository.save(userSessionEntity);
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

    public long removeNoneUserChannel() {
        Iterable<ChannelSessionEntity> channelSessionEntities = channelSessionRepository.findAll();
        long removedChannel = 0;
        for (ChannelSessionEntity channelSessionEntity : channelSessionEntities) {
            Iterable<UserSessionEntity> users = userSessionRepository.findAllByChannelId(channelSessionEntity.getId());
            Stream<UserSessionEntity> userStream = StreamSupport.stream(users.spliterator(), false);

            if (userStream.findAny().isEmpty()) {
                log.info("Remove Channel : {}", channelSessionEntity.getId());
                channelSessionRepository.deleteById(channelSessionEntity.getId());
                removedChannel++;
            }
        }

        return removedChannel;
    }

    public long countUser() {
        return userSessionRepository.count();
    }

    public long countChannel() {
        return channelSessionRepository.count();
    }

}