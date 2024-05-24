package com.egatrap.partage.service;

import com.egatrap.partage.constants.ChannelPermissionType;
import com.egatrap.partage.constants.ChannelRoleType;
import com.egatrap.partage.model.dto.*;
import com.egatrap.partage.model.entity.*;
import com.egatrap.partage.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("channelService")
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ChannelRoleMappingRepository channelRoleMappingRepository;
    private final ChannelPermissionMappingRepository channelPermissionMappingRepository;
    private final PlaylistRepository playlistRepository;
    private final ModelMapper modelMapper;

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
            channelPermissionMappingId.setPermissionId(channelPermissionType.getPERMISSION_ID());

            ChannelPermissionMappingEntity channelPermissionMapping = new ChannelPermissionMappingEntity(channelPermissionMappingId, new ChannelPermissionEntity(channelPermissionType), channel);
            channelPermissionMappingRepository.save(channelPermissionMapping);
        }

        // set usersInfo
        List<ChannelUserInfoDto> channelUserInfo = channelRoleMappingRepository.findActiveUsersByChannelNo(channel.getChannelNo());
        // set channelInfo
        ChannelInfoDto channelInfo = setChannelInfo(channel);

        // response 생성
        ResponseCreateChannelDto responseCreateChannelDto = new ResponseCreateChannelDto();
        responseCreateChannelDto.setChannel(channelInfo);
        responseCreateChannelDto.setChannelUsers(channelUserInfo);
        responseCreateChannelDto.setChannelPermissionTypes(channelPermissionTypes);

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
    public Boolean isActiveChannelByOwnerUserNoAndChannelNo(Long userNo, Long channelNo) {
        return channelRoleMappingRepository.isActiveChannelByOwnerUserNoAndChannelNo(userNo, ChannelRoleType.ROLE_OWNER.getROLE_ID(), channelNo);
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
        responseUpdateChannelDto.setChannel(channelInfo);
        return responseUpdateChannelDto;
    }

    @Transactional
    public void deleteChannel(Long channelNo) {

        // 채널 조회
        ChannelEntity channel = channelRepository.findById(channelNo).orElseThrow(
                () -> new NoSuchElementException());

        channel.deleteChannel();
        channelRepository.save(channel);

        // ToDo. 채널이 종료되면 이후에 추가 로직이 필요하지 않은지
        //  - 채널 권한 매핑 설정 테이블의 활성화 상태 == flase
        //  - 채팅 로그 백업 등
    }

    @Transactional
    public Boolean isActiveChannelByUserNoAndChannelNo(Long userNo, Long channelNo) {
        return channelRoleMappingRepository.isActiveChannelByUserNoAndChannelNo(userNo, channelNo);
    }

    @Transactional
    public ResponseGetChannelDetailInfoDto getChannelDetailInfo(Long userNo, Long channelNo) {

        // 채널 정보 조회
        ChannelEntity channel = channelRepository.findById(channelNo).get();
        ChannelInfoDto channelInfo = setChannelInfo(channel);

        // 사용자 정보 조회
        UserEntity user = userRepository.findById(userNo).get();
        ChannelUserInfoDto channelUserInfo = channelRoleMappingRepository.findActiveUserByChannelNoAndUserNo(channelNo, userNo);
        
        // 채널 사용자 목록 정보 조회
        List<ChannelUserInfoDto> channelUserInfos = channelRoleMappingRepository.findActiveUsersByChannelNo(channel.getChannelNo());

        // 플레이리스트 목록 정보 조회
        List<PlaylistDto> playlistInfos = playlistRepository.findAllByChannel_ChannelNoAndIsActiveOrderBySequence(channelNo, true)
                .stream()
                .map(playlistEntity -> modelMapper.map(playlistEntity, PlaylistDto.class))
                .toList();

        // 채널 권한 설정 목록 정보 조회
        List<ChannelPermissionType> channelPermissionTypes = channelPermissionMappingRepository.findByChannel_ChannelNo(channelNo)
                .stream()
                .map(channelPermissionMappingEntity -> {
                    String permissionId = channelPermissionMappingEntity.getPermission().getPermissionId();
                    return ChannelPermissionType.of(permissionId);
                })
                .collect(Collectors.toList());

        ResponseGetChannelDetailInfoDto responseDto = new ResponseGetChannelDetailInfoDto();
        responseDto.setChannel(channelInfo);
        responseDto.setUser(channelUserInfo);
        responseDto.setChannelUsers(channelUserInfos);
        responseDto.setPlaylists(playlistInfos);
        responseDto.setChannelPermissionTypes(channelPermissionTypes);

        return responseDto;

        // ToDo. 추후 추가 예정 (Redis)
        //  - 채팅 정보
        //  - 현재 플레이중인 영상 정보
    }
}
