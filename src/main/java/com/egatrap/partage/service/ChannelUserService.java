package com.egatrap.partage.service;


import com.egatrap.partage.common.util.CodeGenerator;
import com.egatrap.partage.constants.ChannelRoleType;
import com.egatrap.partage.model.dto.ChannelSessionDto;
import com.egatrap.partage.model.entity.ChannelSessionEntity;
import com.egatrap.partage.model.entity.UserEntity;
import com.egatrap.partage.model.entity.UserSessionEntity;
import com.egatrap.partage.model.vo.UserSession;
import com.egatrap.partage.repository.ChannelSessionRepository;
import com.egatrap.partage.repository.ChannelUserRepository;
import com.egatrap.partage.repository.UserRepository;
import com.egatrap.partage.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@Service("channelUserService")
public class ChannelUserService {

    private final UserSessionRepository userSessionRepository;
    private final ChannelUserRepository channelUserRepository;
    private final ChannelSessionRepository channelSessionRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserSession addUserSession(String userId, String channelId) {
        return addUserSession(userId, channelId, ChannelRoleType.ROLE_VIEWER);
    }

    @Transactional
    public UserSession addUserSession(String userId, String channelId, ChannelRoleType channelRole) {
        String sessionId = CodeGenerator.generateID("WS-");
        LocalDateTime now = LocalDateTime.now();

        UserEntity user = userRepository.findById(userId).orElse(null);

        // 비회원 & 회원 구분
        if (user == null || "NONE".equals(user.getUserId())) {
//            비회원일 경우 레디스에 세션 정보 저장
            UserSessionEntity userSession = UserSessionEntity.builder()
                    .id(sessionId)
                    .userId("NONE")
                    .nickname(user == null ? "NONE" : user.getNickname())
                    .channelId(channelId)
                    .channelRole(ChannelRoleType.ROLE_NONE)
                    .joinTime(LocalDateTime.now())
                    .lastAccessTime(LocalDateTime.now())
                    .build();
            userSessionRepository.save(userSession);
        } else {
//            회원인 경우 디비에 세션 정보 저장
            channelUserRepository.saveChannelUser(
                    sessionId,
                    channelId,
                    userId,
                    channelRole.getROLE_ID(),
                    true,
                    now,
                    now);
        }

        return UserSession.builder()
                .id(sessionId)
                .userId(userId)
                .channelId(channelId)
                .role(channelRole)
                .nickname(user == null ? "" : user.getNickname())
                .lastAccessTime(now)
                .createdAt(now)
                .build();
    }

    public void removeUserSession(String sessionId) {
        log.debug("Redis User removed from channel: sessionId={}", sessionId);
        userSessionRepository.deleteById(sessionId);
    }

    public UserSession getUserSession(String sessionId, String channelId, String userId) {
        UserSessionEntity userSessionEntity = userSessionRepository.findById(sessionId).orElse(null);

        if (userSessionEntity == null) {
            throw new IllegalArgumentException("UserSessionEntity not found: sessionId=" + sessionId);
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
                .createdAt(userSessionEntity.getJoinTime())
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

    public boolean isExistsChannel(String channelId) {
        return channelSessionRepository.existsById(channelId);
    }

    public void createChannelSession(String channelId) {
        channelSessionRepository.save(ChannelSessionEntity.builder()
                .id(channelId)
                .isPlaying(false)
                .playTime(0)
                .lastAccessTime(LocalDateTime.now())
                .build());
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