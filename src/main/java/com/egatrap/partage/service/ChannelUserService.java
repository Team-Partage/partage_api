package com.egatrap.partage.service;


import com.egatrap.partage.common.util.CodeGenerator;
import com.egatrap.partage.constants.ChannelRoleType;
import com.egatrap.partage.model.dto.*;
import com.egatrap.partage.model.entity.*;
import com.egatrap.partage.model.vo.UserSession;
import com.egatrap.partage.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ChannelUserRepository channelUserRepository;
    private final ChannelSessionRepository channelSessionRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public UserSession joinUser(String channelId, String userId) {
        ChannelUserId channelUserId = new ChannelUserId(channelId, userId);
        ChannelUserEntity user = channelUserRepository.findById(channelUserId).orElse(null);
        log.debug("[userEntitiy]=[{}]", user);
        log.debug("User joined to channel: channelId={}, userId={}", channelId, userId);
        if (user != null) {
            user.increaseOnlineCount();
            user.updateLastAccessAt();
            channelUserRepository.save(user);
            return new UserSession(user);
        } else {
            return addUserSession(userId, channelId);
        }
    }

    @Transactional
    public void leaveUser(String channelId, String userId) {
        ChannelUserId channelUserId = new ChannelUserId(channelId, userId);

        // Optional을 사용하여 null 체크 개선
        Optional<ChannelUserEntity> userOptional = channelUserRepository.findById(channelUserId);

        // ifPresent를 사용하여 user가 존재할 때만 로직 실행
        userOptional.ifPresent(user -> {
            user.decreaseOnlineCount();
            channelUserRepository.save(user);
            log.debug("User removed from channel: channelId={}, userId={}", channelId, userId);
        });
    }

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
                    1L,
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

    public UserSession getUserSession(String sessionId) {
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
                .updateTime(LocalDateTime.now())
                .lastAccessTime(LocalDateTime.now())
                .build());
    }

    public CountViewerDto countUserByChannel(String channelId) {
        Iterable<UserSessionEntity> users = userSessionRepository.findAllByChannelId(channelId);

        // 채널 사용자 수 조회 : 로그인 사용자 수 (중복 접속은 1명으로 처리)
        long loginUsers = channelUserRepository
                .countById_ChannelIdAndOnlineCountGreaterThan(channelId, 0);

        // 채널 사용자 수 조회 : 비회원 사용자 수
        long anonymousUsers = StreamSupport.stream(users.spliterator(), false).count();

        return new CountViewerDto(loginUsers, anonymousUsers);
    }

    /**
     * 채널 조회수 동기화 : 레디스에 생성된 유저세선을 이용해 조회수를 동기화
     * @return 동기화 된 채널 수
     */
    public long syncChannelViewerCount() {

        long updatedChannel = 0;

        // 시청자 수가 1이상이 채널 조회
        List<ChannelEntity> channels = channelRepository.findByViewerCountGreaterThan(0);

        // 기존 채널에 저장된 시청자 수와 현재 시청자 수가 다른 경우 동기화
        for (ChannelEntity channel : channels) {
//            Iterable<UserSessionEntity> users = userSessionRepository.findAllByChannelId(channel.getChannelId());
            long viewerCount = countUserByChannel(channel.getChannelId()).getTotalUsers();
            if (channel.getViewerCount() != viewerCount) {
                channel.setViewerCount(Integer.parseInt(String.valueOf(viewerCount))); // long 으로 수정
                channelRepository.save(channel);
                updatedChannel++;
            }
        }

        // 캐싱된 채널에 대한 시청자 수 동기화
        Iterable<ChannelSessionEntity> channelSessions = channelSessionRepository.findAll();
        for (ChannelSessionEntity channelSession : channelSessions) {
            long viewerCount = countUserByChannel(channelSession.getId()).getTotalUsers();

            ChannelEntity channel = channelRepository.findById(channelSession.getId()).orElse(null);
            if (channel != null && channel.getViewerCount() != viewerCount) {
                channel.setViewerCount(Integer.parseInt(String.valueOf(viewerCount))); // long 으로 수정
                channelRepository.save(channel);
                updatedChannel++;
            }
        }

        return updatedChannel;
    }

//    public long removeNoneUserChannel() {
//        Iterable<ChannelSessionEntity> channelSessionEntities = channelSessionRepository.findAll();
//        long removedChannel = 0;
//        for (ChannelSessionEntity channelSessionEntity : channelSessionEntities) {
//            Iterable<UserSessionEntity> users = userSessionRepository.findAllByChannelId(channelSessionEntity.getId());
//            Stream<UserSessionEntity> userStream = StreamSupport.stream(users.spliterator(), false);
//
//            if (userStream.findAny().isEmpty()) {
//                log.info("Remove Channel : {}", channelSessionEntity.getId());
//                channelSessionRepository.deleteById(channelSessionEntity.getId());
//                removedChannel++;
//            }
//        }
//        return removedChannel;
//    }

    public long countUser() {
        // 채널 사용자 수 조회 : 로그인 사용자 수 (중복 접속은 1명으로 처리)
        long loginUsers = channelUserRepository.countByOnlineCountGreaterThan(0);
        return userSessionRepository.count() + loginUsers;
    }

    public long countChannel() {
        return channelSessionRepository.count();
    }

    public ResponseChannelUsersDto getChannelUsers(String channelId, int cursor, int perPage) {

        // 정렬 조건 - default: 권한 아이디
        // Pageable 객체 생성
        Sort sort = Sort.by(Sort.Direction.ASC, "role.roleId");
        PageRequest pageRequest = PageRequest.of(cursor - 1, perPage, sort);

        Page<ChannelUserEntity> pagingChannelUsers = channelUserRepository.findByChannel_ChannelIdAndOnlineCountGreaterThanOne(channelId, pageRequest);

        // 페이지 정보 생성
        PageInfoDto page = PageInfoDto.builder()
                .cursor(cursor)
                .perPage(perPage)
                .totalPage(pagingChannelUsers.getTotalPages())
                .totalCount(pagingChannelUsers.getTotalElements()).build();

        List<ChannelUserDto> channelUsers = new ArrayList<>();
        for (ChannelUserEntity channelUser : pagingChannelUsers) {
            channelUsers.add(ChannelUserDto.builder()
                    .roleId(channelUser.getRole().getRoleId())
                    .userId(channelUser.getUser().getUserId())
                    .email(channelUser.getUser().getEmail())
                    .nickname(channelUser.getUser().getNickname())
                    .profileColor(channelUser.getUser().getProfileColor())
                    .profileImage(channelUser.getUser().getProfileImage())
                    .build());
        }

        return ResponseChannelUsersDto.builder()
                .users(channelUsers)
                .page(page)
                .build();
    }
}