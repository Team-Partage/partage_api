package com.egatrap.partage.service;

import com.egatrap.partage.common.util.CodeGenerator;
import com.egatrap.partage.constants.ChannelColorType;
import com.egatrap.partage.constants.ChannelRoleType;
import com.egatrap.partage.constants.ChannelType;
import com.egatrap.partage.exception.BadRequestException;
import com.egatrap.partage.model.dto.*;
import com.egatrap.partage.model.entity.*;
import com.egatrap.partage.repository.*;
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
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service("channelService")
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelCacheDataRepository channelCacheDataRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ChannelRoleMappingRepository channelRoleMappingRepository;
    private final ChannelPermissionRepository channelPermissionRepository;
    private final PlaylistRepository playlistRepository;
    // private final ChannelSearchRepository channelSearchRepository;
    private final ModelMapper modelMapper;


    public void joinUserToChannel(String channelId, String userId) {

        ChannelCacheDataEntity channel = channelCacheDataRepository.findById(channelId)
                .orElseThrow(() -> new BadRequestException("Channel not found. channelId=" + channelId));

        // 사용자 정보 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found. userId=" + userId));


        // 사용자 추가
        channel.getUsers().add(modelMapper.map(user, UserDto.class));
    }

    public void leaveUserFromChannel(String channelId, String userId) {

        ChannelCacheDataEntity channel = channelCacheDataRepository.findById(channelId)
                .orElseThrow(() -> new BadRequestException("Channel not found. channelId=" + channelId));

        // 사용자 정보 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found. userId=" + userId));

        // 사용자 제거
        channel.getUsers().remove(modelMapper.map(user, UserDto.class));
    }

    public ChannelCacheDataDto getChannelCacheData(String channelId) {
        // 채널 캐시 데이터 조회 없을 경우 생성
        if (!channelCacheDataRepository.existsById(channelId))
            return createChannelCacheData(channelId);

        ChannelCacheDataEntity channel = channelCacheDataRepository.findById(channelId)
                .orElseThrow(() -> new BadRequestException("Channel not found. channelId=" + channelId));

        return modelMapper.map(channel, ChannelCacheDataDto.class);
    }

    public ChannelCacheDataDto createChannelCacheData(String channelId) {

        if (channelCacheDataRepository.existsById(channelId))
            throw new BadRequestException("Channel cache data already exists. channelId=" + channelId);

        ChannelCacheDataEntity channel = ChannelCacheDataEntity.builder()
                .id(channelId)
                .users(new ArrayList<>())
                .currentPlayTime(0)
                .lastUpdateAt(LocalDateTime.now())
                .build();

        channelCacheDataRepository.save(channel);

        return modelMapper.map(channel, ChannelCacheDataDto.class);
    }

    public void removeChannelCacheData(String channelId) {
        channelCacheDataRepository.deleteById(channelId);
    }

    public int currentUsersCountToChannel(String channelId) {
        ChannelCacheDataEntity channel = channelCacheDataRepository.findById(channelId)
                .orElseThrow(() -> new BadRequestException("Channel not found. channelId=" + channelId));

        return channel.getUsers().size();
    }

    public boolean isExistsChannel(String channelId) {
        // 채널아이디를 이용해 해당 채널이 존재하고 엑티브 상태인지 확인
        return channelRepository.existsByChannelIdAndIsActive(channelId, true);
    }

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
                .channelColor(ChannelColorType.getChannelColor(params.getChannelColor()))
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

        // set usersInfo
        List<ChannelUserDto> channelUserInfo = channelRoleMappingRepository.findActiveUsersByChannelId(channel.getChannelId());
        // set channelInfo
        ChannelDto channelInfo = setChannelInfo(channel);

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

    private ChannelDto setChannelInfo(ChannelEntity channel) {
        ChannelDto channelInfo = new ChannelDto();
        channelInfo.setChannelId(channel.getChannelId());
        channelInfo.setName(channel.getName());
        channelInfo.setType(channel.getType());
        channelInfo.setHashtag(channel.getHashtag());
        channelInfo.setChannelUrl(channel.getChannelUrl());
        channelInfo.setChannelColor(ChannelColorType.getChannelColor(channel.getChannelColor()));
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
        ChannelEntity channel = channelRepository.findById(channelId).orElseThrow(() -> new NoSuchElementException());

        // 채널 update
        channel.updateChannelInfo(params.getName(),
                params.getType(),
                params.getHashtag(),
                ChannelColorType.getChannelColor(params.getChannelColor()));
        channelRepository.save(channel);

        // response 생성
        ResponseUpdateChannelDto responseUpdateChannelDto = new ResponseUpdateChannelDto();
        ChannelDto channelInfo = setChannelInfo(channel);
        responseUpdateChannelDto.setChannel(channelInfo);
        return responseUpdateChannelDto;
    }

    @Transactional
    public void deleteChannel(String channelId) {

        // 채널 조회
        ChannelEntity channel = channelRepository.findById(channelId).orElseThrow(() -> new NoSuchElementException());

        channel.deleteChannel();
        channelRepository.save(channel);

        // 채널 사용자 모두 제거
        channelRoleMappingRepository.deleteByChannel_ChannelId(channelId);

        // ToDo. 채널이 종료되면 이후에 추가 로직이 필요하지 않은지
        //  - 채팅 로그 백업 등
    }

    @Transactional
    public Boolean isActiveChannelByUserIdAndChannelId(String userId, String channelId) {
        return channelRoleMappingRepository.isActiveChannelByUserIdAndChannelId(userId, channelId);
    }

    public ChannelDto getChannel(String channelId) {
        ChannelEntity channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException());
        return setChannelInfo(channel);
    }

    public List<ChannelUserDto> getChannelUsers(String channelId) {
        return channelRoleMappingRepository.findActiveUsersByChannelId(channelId);
    }

    public ChannelPermissionInfoDto getChannelPermission(String channelId) {
        ChannelPermissionEntity channelPermission = channelPermissionRepository.findById(channelId)
                .orElseThrow(() -> new BadRequestException("Channel permission config not found."));

        return new ChannelPermissionInfoDto(channelPermission);
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

    public ResponseSearchChannelsDto getSearchChannels(int cursor, int perPage, String keyword) {

        // 정렬 조건 - default: 최근 생성일
        // Pageable 객체 생성
        Sort sort = Sort.by(Sort.Direction.DESC, "createAt");
        PageRequest pageRequest = PageRequest.of(cursor - 1, perPage, sort);

        Page<ChannelEntity> pagingChannels = null;
        // 키워드가 없는 경우 - 전체 목록 조회
        if (keyword == null || keyword.trim().isEmpty())
            pagingChannels = channelRepository.findByTypeAndIsActive(ChannelType.PUBLIC, true, pageRequest);
        else
            pagingChannels = channelRepository.findBySearchKeywordAndIsActive(keyword, true, pageRequest);

        // 페이지 정보 생성
        PageInfoDto page = PageInfoDto.builder()
                .cursor(cursor)
                .perPage(perPage)
                .totalPage(pagingChannels.getTotalPages())
                .totalCount(pagingChannels.getTotalElements()).build();

        // 채널 검색 정보 목록 생성
        List<ChannelSearchDto> channels = pagingChannels.stream()
                .map(channelEntity -> {

                    // 채널 정보
                    ChannelDto channelDto = modelMapper.map(channelEntity, ChannelDto.class);

                    // 현재 재생중인 플레이리스트 정보
                    PlaylistDto playlistDto = null;
                    Optional<PlaylistEntity> playlistEntity =
                            playlistRepository.findFirstByChannel_ChannelIdAndIsActiveAndSequence(channelDto.getChannelId(), true, 0);
                    if (playlistEntity.isPresent())
                        playlistDto = modelMapper.map(playlistEntity.get(), PlaylistDto.class);

                    // 채널 생성자 정보
                    UserDto userDto = null;
                    Optional<ChannelRoleMappingEntity> channelRoleEntity =
                            channelRoleMappingRepository.findByChannel_ChannelIdAndRole_RoleId(channelDto.getChannelId(), ChannelRoleType.ROLE_OWNER.getROLE_ID());
                    if (channelRoleEntity.isPresent())
                        userDto = modelMapper.map(channelRoleEntity.get().getUser(), UserDto.class);

                    return ChannelSearchDto.builder()
                            .channel(channelDto)
                            .playlist(playlistDto)
                            .owner(userDto)
                            .user_count(new Random().nextInt(200) + 1)
                            .build();
                })
                .toList();

        // response 생성
        ResponseSearchChannelsDto response = new ResponseSearchChannelsDto();
        response.setPage(page);
        response.setChannels(channels);
        return response;
    }
}
