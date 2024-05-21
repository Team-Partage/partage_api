package com.egatrap.partage.service;

import com.egatrap.partage.constants.ChannelRoleType;
import com.egatrap.partage.model.dto.RequestCreateChannelDto;
import com.egatrap.partage.model.entity.*;
import com.egatrap.partage.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service("channelService")
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ChannelRoleMappingRepository channelRoleMappingRepository;
    private final ChannelPermissionMappingRepository channelPermissionMappingRepository;

    @Transactional
    public boolean isActiveChannelByUserNo(Long userNo) {

        return channelRoleMappingRepository.isExistsActiveChannelByUserNo(userNo, ChannelRoleType.ROLE_OWNER.getROLE_ID());
    }

    @Transactional
    public void createChannel(RequestCreateChannelDto params, Long userNo) {

        // 채널 생성 및 저장
        ChannelEntity channel = params.toEntity(makeChannelUrl());
        channelRepository.save(channel);

        // 사용자 정보 조회
        UserEntity user = userRepository.findById(userNo).get();
        // 채널 권한 생성
        ChannelRoleEntity channelRole = new ChannelRoleEntity(ChannelRoleType.ROLE_OWNER);

        // 채널 권한 매핑 생성 및 저장
        ChannelRoleMappingId channelRoleMappingId = new ChannelRoleMappingId();
        channelRoleMappingId.setRoleId(channelRole.getRoleId());
        channelRoleMappingId.setChannelNo(channel.getChannelNo());
        channelRoleMappingId.setUserNo(user.getUserNo());
        ChannelRoleMappingEntity channelRoleMapping = new ChannelRoleMappingEntity(channelRoleMappingId, channel, user, channelRole);
        channelRoleMappingRepository.save(channelRoleMapping);

        // 디폴트 채널 권한 설정 목록 생성
        List<ChannelPermissionEntity> channelPermissionEntityList = new ChannelPermissionEntity().defaultChannelPermissionList();

        // 채널 권한 설정 매핑 생성 및 저장
        for (ChannelPermissionEntity channelPermission : channelPermissionEntityList) {

            ChannelPermissionMappingId channelPermissionMappingId = new ChannelPermissionMappingId();
            channelPermissionMappingId.setChannelNo(channel.getChannelNo());
            channelPermissionMappingId.setPermissionId(channelPermission.getPermissionId());

            ChannelPermissionMappingEntity channelPermissionMapping = new ChannelPermissionMappingEntity(channelPermissionMappingId, channelPermission, channel);
            channelPermissionMappingRepository.save(channelPermissionMapping);
        }
    }

    private String makeChannelUrl() {

        byte[] uuidStringBytes = UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(uuidStringBytes);
            String encodeResult = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);

            return encodeResult.substring(0, 12);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
