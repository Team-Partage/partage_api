package com.egatrap.partage.service;

import com.egatrap.partage.constants.ChannelRoleType;
import com.egatrap.partage.constants.ChannelType;
import com.egatrap.partage.model.dto.ChannelPermissionDto;
import com.egatrap.partage.model.entity.*;
import com.egatrap.partage.repository.ChannelPermissionRepository;
import com.egatrap.partage.repository.ChannelRepository;
import com.egatrap.partage.repository.ChannelRoleMappingRepository;
import com.egatrap.partage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service("channelPermissionService")
public class ChannelPermissionService {

    private final ChannelRoleMappingRepository channelRoleMappingRepository;
    private final ChannelPermissionRepository channelPermissionRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    /**
     * Get channel permission
     *
     * @param channelId channel id
     * @return ChannelPermissionDto
     */
    public ChannelPermissionDto getChannelPermission(String channelId) {
        Optional<ChannelPermissionEntity> channelPermission = channelPermissionRepository.findByChannelId(channelId);
        return channelPermission.map(channelPermissionEntity -> ChannelPermissionDto.builder()
                .channelId(channelPermissionEntity.getChannelId())
                .playlistAdd(ChannelRoleType.get(channelPermissionEntity.getPlaylistAdd()))
                .playlistRemove(ChannelRoleType.get(channelPermissionEntity.getPlaylistRemove()))
                .playlistMove(ChannelRoleType.get(channelPermissionEntity.getPlaylistMove()))
                .videoPlay(ChannelRoleType.get(channelPermissionEntity.getVideoPlay()))
                .videoSeek(ChannelRoleType.get(channelPermissionEntity.getVideoSeek()))
                .videoSkip(ChannelRoleType.get(channelPermissionEntity.getVideoSkip()))
                .chatSend(ChannelRoleType.get(channelPermissionEntity.getChatSend()))
                .chatDelete(ChannelRoleType.get(channelPermissionEntity.getChatDelete()))
                .ban(ChannelRoleType.get(channelPermissionEntity.getBan()))
                .build()).orElse(null);
    }

    /**
     * Get channel role by channel id and user id
     *
     * @param channelId channel id
     * @param userId    user id
     * @return ChannelRoleType
     */
    public ChannelRoleType getChannelRole(String channelId, String userId) {
        ChannelRoleMappingEntity userChannelRole =
                channelRoleMappingRepository.findByChannelIdAndUserId(channelId, userId).orElse(null);

        // 해당 채널에 권한이 없는 경우 초기 생성
        if (userChannelRole == null) {
            createChannelRole(channelId, userId, ChannelRoleType.ROLE_VIEWER);
            return ChannelRoleType.ROLE_VIEWER;
        } else {
            return ChannelRoleType.get(userChannelRole.getRole().getRoleId());
        }
    }

    public void createChannelRole(String channelId, String userId, ChannelRoleType role) {
        // 해당 채널에 권한이 이미 존재하는지 확인
        if (isExistChannelRole(channelId, userId)) {
            log.warn("Channel role already exists. channelId: {}, userId: {}", channelId, userId);
            return;
        }

        // 채널 엔티티 가져오기
        ChannelEntity channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("Channel not found: " + channelId));

        // 유저 엔티티 가져오기
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        log.info("Channel role created. channelId: {}, userId: {}, role: {}", channelId, userId, role);
        ChannelRoleMappingEntity channelRoleMappingEntity = ChannelRoleMappingEntity.builder()
                .id(new ChannelRoleMappingId(channelId, userId))
                .channel(channel)
                .user(user)
                .role(new ChannelRoleEntity(role))
                .build();

        channelRoleMappingRepository.save(channelRoleMappingEntity);
    }

    public boolean isExistChannelRole(String channelId, String userId) {
        return channelRoleMappingRepository.existsById(new ChannelRoleMappingId(channelId, userId));
    }
}
