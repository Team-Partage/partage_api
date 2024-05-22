package com.egatrap.partage.service;

import com.egatrap.partage.constants.ChannelPermissionType;
import com.egatrap.partage.constants.ChannelRoleType;
import com.egatrap.partage.model.dto.*;
import com.egatrap.partage.model.entity.*;
import com.egatrap.partage.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Slf4j
@Service("channelService")
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ChannelRoleMappingRepository channelRoleMappingRepository;
    private final ChannelPermissionMappingRepository channelPermissionMappingRepository;

    @Transactional
    public boolean isExistsActiveChannelByUserNo(Long userNo) {
        return channelRoleMappingRepository.isExistsActiveChannelByUserNo(userNo, ChannelRoleType.ROLE_OWNER.getROLE_ID());
    }

    @Transactional
    public ResponseCreateChannelDto createChannel(RequestCreateChannelDto params, Long userNo) {
        // 채널 생성 및 저장
        ChannelEntity channel = params.toEntity(makeChannelUrl());
        channelRepository.save(channel);

        // 사용자 정보 조회
        UserEntity user = userRepository.findById(userNo).get();

        // 채널 권한 생성, 채널 권한 매핑 생성 및 저장
        ChannelRoleMappingEntity channelRoleMapping = getChannelRoleMappingEntity(channel, user, ChannelRoleType.ROLE_OWNER);
        channelRoleMappingRepository.save(channelRoleMapping);

        // 채널 Default Permmission 설정 생성 및 저장
        List<ChannelPermissionType> channelPermissionTypes = ChannelPermissionType.defaultChannelPermissionList();
        for (ChannelPermissionType channelPermissionType : channelPermissionTypes) {

            ChannelPermissionMappingId channelPermissionMappingId = new ChannelPermissionMappingId();
            channelPermissionMappingId.setChannelNo(channel.getChannelNo());
            channelPermissionMappingId.setPermissionId(channelPermissionType.getROLE_ID());

            ChannelPermissionMappingEntity channelPermissionMapping = new ChannelPermissionMappingEntity(channelPermissionMappingId, new ChannelPermissionEntity(channelPermissionType), channel);
            channelPermissionMappingRepository.save(channelPermissionMapping);
        }

        // set userInfo
        List<ChannelUserInfoDto> channelUserInfo = channelRoleMappingRepository.findActiveUsersByChannelNo(channel.getChannelNo());
        // set channelInfo
        ChannelInfoDto channelInfo = setChannelInfo(channel);

        // response 생성
        ResponseCreateChannelDto responseCreateChannelDto = new ResponseCreateChannelDto();
        responseCreateChannelDto.setChannelInfo(channelInfo);
        responseCreateChannelDto.setUerInfo(channelUserInfo);
        responseCreateChannelDto.setChannelPermissionTypeInfo(channelPermissionTypes);

        return responseCreateChannelDto;
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

    private ChannelRoleMappingEntity getChannelRoleMappingEntity(ChannelEntity channel, UserEntity user, ChannelRoleType roleType) {
        ChannelRoleEntity channelRole = new ChannelRoleEntity(roleType);

        ChannelRoleMappingId channelRoleMappingId = new ChannelRoleMappingId();
        channelRoleMappingId.setRoleId(channelRole.getRoleId());
        channelRoleMappingId.setChannelNo(channel.getChannelNo());
        channelRoleMappingId.setUserNo(user.getUserNo());

        return new ChannelRoleMappingEntity(channelRoleMappingId, channel, user, channelRole, true);
    }

    private ChannelInfoDto setChannelInfo(ChannelEntity channel) {
        ChannelInfoDto channelInfo = new ChannelInfoDto();
        channelInfo.setChannelNo(channel.getChannelNo());
        channelInfo.setName(channel.getName());
        channelInfo.setType(channel.getType());
        channelInfo.setHashtag(channel.getHashtag());
        channelInfo.setChannelUrl(channel.getChannelUrl());
        channelInfo.setChannelColor(channel.getChannelColor());
        channelInfo.setCreateAt(channel.getCreateAt());

        return channelInfo;
    }

    @Transactional
    public boolean isActiveChannelByUserNoAndChannelNo(Long userNo, Long channelNo) {
        return channelRoleMappingRepository.isActiveChannelByUserNoAndChannelNo(userNo, ChannelRoleType.ROLE_OWNER.getROLE_ID(), channelNo);
    }

    @Transactional
    public ResponseUpdateChannelDto updateChannel(Long channelNo, RequestUpdateChannelDto params) {

        // 채널 조회
        ChannelEntity channel = channelRepository.findById(channelNo).orElseThrow(
                () -> new NoSuchElementException());

        // 채널 update
        channel.updateChannelInfo(params.getName(),
                                  params.getType(),
                                  params.getHashtag(),
                                  params.getChannelColor());
        channelRepository.save(channel);

        // response 생성
        ResponseUpdateChannelDto responseUpdateChannelDto = new ResponseUpdateChannelDto();
        ChannelInfoDto channelInfo = setChannelInfo(channel);
        responseUpdateChannelDto.setChannelInfo(channelInfo);
        return responseUpdateChannelDto;
    }

    public void deleteChannel(Long channelNo) {

        // 채널 조회
        ChannelEntity channel = channelRepository.findById(channelNo).orElseThrow(
                () -> new NoSuchElementException());

        channel.deleteChannel();
        channelRepository.save(channel);

        // ToDo. 채널이 종료되면 이후에 추가 로직이 필요하지 않은지
        //  - 채팅 로그 백업 등
    }
}
