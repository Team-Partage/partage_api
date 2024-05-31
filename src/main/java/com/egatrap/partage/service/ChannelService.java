package com.egatrap.partage.service;

import com.egatrap.partage.common.util.CodeGenerator;
import com.egatrap.partage.constants.ChannelRoleType;
import com.egatrap.partage.constants.ChannelType;
import com.egatrap.partage.exception.BadRequestException;
import com.egatrap.partage.model.dto.*;
import com.egatrap.partage.model.entity.*;
import com.egatrap.partage.repository.*;
import com.google.api.services.youtube.model.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private final ChannelPermissionRepository channelPermissionRepository;
    private final PlaylistRepository playlistRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public boolean isExistsActiveChannelByUserId(String userId) {
        return channelRoleMappingRepository.isExistsActiveChannelByUserId(userId, ChannelRoleType.ROLE_OWNER.getROLE_ID());
    }

    @Transactional
    public ResponseCreateChannelDto createChannel(RequestCreateChannelDto params, String userId) {
        // 채널 생성 및 저장
        ChannelEntity channel = ChannelEntity.builder()
                .channelId(CodeGenerator.generateID("C"))
                .name(params.getName())
                .type(params.getType())
                .hashtag(params.getHashtag())
                .channelColor(params.getChannelColor())
                .channelUrl(makeChannelUrl())
                .isActive(true)
                .build();
        channelRepository.save(channel);

        // 사용자 정보 조회
        UserEntity user = userRepository.findById(userId).get();

        // 채널 권한 생성, 채널 권한 매핑 생성 및 저장
        ChannelRoleMappingEntity channelRoleMapping = getChannelRoleMappingEntity(channel, user, ChannelRoleType.ROLE_OWNER);
        channelRoleMappingRepository.save(channelRoleMapping);


        // 일대일 관계에서 관계 재설정을 위함. (https://pasudo123.tistory.com/493)
        //  - DB에 해당 식별자로 데이터가 저장되어 있음에도 불구하고 또 저장해서 에러가 발생
        //  -> DB에 해당 식별자로 있는 지 한번 더 조회해서 반환된 객체를 기준으로 관계를 재설정
        channel = channelRepository.findById(channel.getChannelId()).get();

        // 채널 Permmission 설정 생성 및 저장
        ChannelPermissionEntity channelPermission = ChannelPermissionEntity.builder()
                .channel(channel)
                .build();
        channelPermissionRepository.save(channelPermission);

        System.out.println("channelPermission = " + channelPermission);

        // set usersInfo
        List<ChannelUserInfoDto> channelUserInfo = channelRoleMappingRepository.findActiveUsersByChannelId(channel.getChannelId());
        // set channelInfo
        ChannelInfoDto channelInfo = setChannelInfo(channel);

        // response 생성
        ResponseCreateChannelDto responseCreateChannelDto = new ResponseCreateChannelDto();
        responseCreateChannelDto.setChannel(channelInfo);
        responseCreateChannelDto.setChannelUsers(channelUserInfo);
        responseCreateChannelDto.setChannelPermissions(new ChannelPermissionInfoDto(channelPermission));

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
        channelRoleMappingId.setChannelId(channel.getChannelId());
        channelRoleMappingId.setUserId(user.getUserId());

        return new ChannelRoleMappingEntity(channelRoleMappingId, channel, user, channelRole);
    }

    private ChannelInfoDto setChannelInfo(ChannelEntity channel) {
        ChannelInfoDto channelInfo = new ChannelInfoDto();
        channelInfo.setChannelId(channel.getChannelId());
        channelInfo.setName(channel.getName());
        channelInfo.setType(channel.getType());
        channelInfo.setHashtag(channel.getHashtag());
        channelInfo.setChannelUrl(channel.getChannelUrl());
        channelInfo.setChannelColor(channel.getChannelColor());
        channelInfo.setCreateAt(channel.getCreateAt());

        return channelInfo;
    }

    @Transactional
    public Boolean isActiveChannelByOwnerUserIdAndChannelId(String userId, String channelId) {
        return channelRoleMappingRepository.isActiveChannelByOwnerUserIdAndChannelId(userId, ChannelRoleType.ROLE_OWNER.getROLE_ID(), channelId);
    }

    @Transactional
    public ResponseUpdateChannelDto updateChannel(String channelId, RequestUpdateChannelDto params) {

        // 채널 조회
        ChannelEntity channel = channelRepository.findById(channelId).orElseThrow(
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
    public void deleteChannel(String channelId) {

        // 채널 조회
        ChannelEntity channel = channelRepository.findById(channelId).orElseThrow(
                () -> new NoSuchElementException());

        channel.deleteChannel();
        channelRepository.save(channel);

        // ToDo. 채널이 종료되면 이후에 추가 로직이 필요하지 않은지
        //  - 채널 권한 매핑 테이블의 사용자 모두 제거 필요
        //  - 채팅 로그 백업 등
    }

    @Transactional
    public Boolean isActiveChannelByUserIdAndChannelId(String userId, String channelId) {
        return channelRoleMappingRepository.isActiveChannelByUserIdAndChannelId(userId, channelId);
    }

    @Transactional
    public ResponseGetChannelDetailInfoDto getChannelDetailInfo(String userId, String channelId) {

        // 채널 정보 조회
        ChannelEntity channel = channelRepository.findById(channelId).get();
        ChannelInfoDto channelInfo = setChannelInfo(channel);

        // 사용자 정보 조회
        UserEntity user = userRepository.findById(userId).get();
        ChannelUserInfoDto channelUserInfo = channelRoleMappingRepository.findActiveUserByChannelIdAndUserId(channelId, userId);
        
        // 채널 사용자 목록 정보 조회
        List<ChannelUserInfoDto> channelUserInfos = channelRoleMappingRepository.findActiveUsersByChannelId(channel.getChannelId());

        // 플레이리스트 목록 정보 조회
        List<PlaylistDto> playlistInfos = playlistRepository.findAllByChannel_ChannelIdAndIsActiveOrderBySequence(channelId, true)
                .stream()
                .map(playlistEntity -> modelMapper.map(playlistEntity, PlaylistDto.class))
                .toList();

        // 채널 권한 설정 목록 정보 조회
        ChannelPermissionEntity channelPermission = channelPermissionRepository.findById(channelId)
                .orElseThrow(() -> new BadRequestException("Channel permission config not found."));

        ResponseGetChannelDetailInfoDto response = new ResponseGetChannelDetailInfoDto();
        response.setChannel(channelInfo);
        response.setUser(channelUserInfo);
        response.setChannelUsers(channelUserInfos);
        response.setPlaylists(playlistInfos);
        response.setChannelPermissions(new ChannelPermissionInfoDto(channelPermission));

        return response;

        // ToDo. 추후 추가 예정 (Redis)
        //  - 채팅 정보
        //  - 현재 플레이중인 영상 정보
    }

    @Transactional
    public void updateUserChannelRole(String channelId, RequestUpdateUserChannelRoleDto params) {
        channelRoleMappingRepository.updateRoleId(channelId, params.getUserId(), params.getRoleId());
    }

    @Transactional
    public void updateChannelPermissions(String channelId, RequestUpdateChannelPermissionsDto params) {

        ChannelPermissionEntity channelPermission = channelPermissionRepository.findById(channelId)
                .orElseThrow(() -> new BadRequestException("Not found channel permission. channelId=" + channelId));

        // 채널 permission update
        channelPermission.onUpdate(params.getChannelPermissions());
        channelPermissionRepository.save(channelPermission);
    }

    @Transactional
    public ResponseGetPublicActiveChannelsDto getActivePublicChannels(int cursor, int perPage) {

        // 정렬 조건 - default: 최근 생성일
        // Pageable 객체 생성
        Sort sort = Sort.by(Sort.Direction.DESC, "createAt");
        PageRequest pageRequest = PageRequest.of(cursor - 1, perPage, sort);

        // 활성화 중인 공개 채널 조회
        Page<ChannelEntity> pagingChannels = channelRepository.findByTypeAndIsActive(ChannelType.PUBLIC, true, pageRequest);

        // 페이지 정보 생성
        PageInfoDto page = PageInfoDto.builder()
                .cursor(cursor)
                .perPage(perPage)
                .totalPage(pagingChannels.getTotalPages())
                .totalCount(pagingChannels.getTotalElements()).build();

        // 채널 목록 정보 생성
        List<ChannelInfoDto> channels = pagingChannels
                .stream()
                .map(channelEntity -> modelMapper.map(channelEntity, ChannelInfoDto.class))
                .toList();

        // response 생성
        ResponseGetPublicActiveChannelsDto response = new ResponseGetPublicActiveChannelsDto();
        response.setPage(page);
        response.setChannels(channels);

        return response;

        // ToDo. 영상 썸네일 정보 추가 전송 필요
        //  - 현재 플레이중인 영상 기준으로 썸네일이 전송되어야 하는데 현재 플레이중인 영상 정보는 추후 레디스에 저장될 예정
    }
}
