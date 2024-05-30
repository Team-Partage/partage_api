package com.egatrap.partage.service;

import com.egatrap.partage.constants.ChannelRoleType;
import com.egatrap.partage.constants.ChannelType;
import com.egatrap.partage.model.dto.ChannelPermissionDto;
import com.egatrap.partage.model.entity.ChannelPermissionEntity;
import com.egatrap.partage.model.entity.ChannelRoleMappingEntity;
import com.egatrap.partage.repository.ChannelPermissionRepository;
import com.egatrap.partage.repository.ChannelRoleMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service("channelPermissionService")
public class ChannelPermissionService {

    private final ChannelRoleMappingRepository channelRoleMappingRepository;
    private final ChannelPermissionRepository channelPermissionRepository;
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

        return userChannelRole != null
                ? ChannelRoleType.get(userChannelRole.getRole().getRoleId()) : ChannelRoleType.ROLE_VIEWER;
    }

}
