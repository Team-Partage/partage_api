package com.egatrap.partage.controller;

import com.egatrap.partage.constants.ChannelRoleType;
import com.egatrap.partage.constants.MessageType;
import com.egatrap.partage.constants.ResponseType;
import com.egatrap.partage.exception.BadRequestException;
import com.egatrap.partage.exception.ConflictException;
import com.egatrap.partage.model.dto.*;
import com.egatrap.partage.model.dto.chat.SendMessageDto;
import com.egatrap.partage.service.*;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.egatrap.partage.controller.StompController.CHANNEL_PREFIX;

@Slf4j
@RestController
@RequestMapping("/api/v1/channel")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;
    private final ChannelPermissionService channelPermissionService;
    private final ChannelUserService channelUserService;
    private final SimpMessagingTemplate messagingTemplate;

    @ApiOperation(value = "채널 생성")
    @PostMapping
    public ResponseEntity<?> createChannel(@Valid @RequestBody RequestCreateChannelDto params) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        // 사용자가 생성한 활성화 채널이 있는지 체크
        //  - 채널이 있다면 409 상태코드 반환
        if (channelService.isExistsActiveChannelByUserId(userId))
            throw new ConflictException("The user's channel already exists.");

        // 채널 생성 및 채널 권한 설정 정보 생성
        ResponseCreateChannelDto responseCreateChannelDto = channelService.createChannel(params, userId);

        // 업데이트된 채널 권한 설정 (채널 매핑 디비 사용 X)
        channelPermissionService.createChannelRole(
                responseCreateChannelDto.getChannel().getChannelId(),
                userId,
                ChannelRoleType.ROLE_OWNER);

        channelUserService.addUserSession(userId, responseCreateChannelDto.getChannel().getChannelId(), ChannelRoleType.ROLE_OWNER);

        return new ResponseEntity<>(responseCreateChannelDto, HttpStatus.OK);
    }

    @ApiOperation(value = "채널 수정")
    @PutMapping("/{channelId}")
    public ResponseEntity<?> updateChannel(@PathVariable("channelId") String channelId,
                                           @RequestBody RequestUpdateChannelDto params) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        // 대상 채널이 활성화 중이고 채널의 OWNER가 요청했는 지 체크
        //  - 채널이 없거나 OWNER가 아닌 사용자가 잘못 요청한 경우 400 상태코드 반환
        if (channelService.isActiveChannelByOwnerUserIdAndChannelId(userId, channelId) == null)
            throw new BadRequestException("Invalid update channel request.");

        // 채널 생성 및 채널 권한 설정 정보 생성
        ResponseUpdateChannelDto responseUpdateChannelDto = channelService.updateChannel(channelId, params);
        return new ResponseEntity<>(responseUpdateChannelDto, HttpStatus.OK);
    }

    @ApiOperation(value = "채널 삭제")
    @DeleteMapping("/{channelId}")
    public ResponseEntity<?> deleteChannel(@PathVariable("channelId") String channelId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        // 대상 채널이 활성화 중이고 채널의 OWNER가 요청했는 지 체크
        //  - 채널이 없거나 OWNER가 아닌 사용자가 잘못 요청한 경우 400 상태코드 반환
        if (channelService.isActiveChannelByOwnerUserIdAndChannelId(userId, channelId) == null)
            throw new BadRequestException("Invalid delete channel request.");

        channelService.deleteChannel(channelId);
        return new ResponseEntity<>(new ResponseDto(ResponseType.SUCCESS), HttpStatus.OK);
    }

    @ApiOperation(value = "채널 상세 정보 조회")
    @GetMapping("/{channelId}")
    public ResponseEntity<?> getChannelDetailInfo(@PathVariable("channelId") String channelId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        ChannelDto channel = channelService.getChannel(channelId);
        ChannelUserDto owner = channelService.getChannelOwner(channelId);
        ChannelUserDto user = channelService.getChannelUser(channelId, userId);
        ChannelPermissionInfoDto channelPermission = channelService.getChannelPermission(channelId);

        ResponseGetChannelDetailInfoDto response = ResponseGetChannelDetailInfoDto.builder()
                .channel(channel)
                .owner(owner)
                .user(user)
                .channelPermissions(channelPermission)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "채널 사용자 권한 수정")
    @PatchMapping("/{channelId}/role")
    public ResponseEntity<?> updateUserChannelRole(@PathVariable("channelId") String channelId,
                                                   @Valid @RequestBody RequestUpdateUserChannelRoleDto params) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        // 채널이 활성화 중이고 요청자 권한 체크
        // 변경 대상 및 변경 권한 체크
        if (channelService.isActiveChannelByOwnerUserIdAndChannelId(userId, channelId) == null)
            System.out.println("111");
        if (channelService.isActiveChannelByUserIdAndChannelId(params.getUserId(), channelId) == null)
            System.out.println("222");
        if (params.getUserId().equals(userId) || params.getRoleId().equals(ChannelRoleType.ROLE_OWNER.getROLE_ID()))
            System.out.println("333");
        if (channelService.isActiveChannelByOwnerUserIdAndChannelId(userId, channelId) == null ||
                channelService.isActiveChannelByUserIdAndChannelId(params.getUserId(), channelId) == null ||
                params.getUserId().equals(userId) || params.getRoleId().equals(ChannelRoleType.ROLE_OWNER.getROLE_ID())) {

            throw new BadRequestException("Invalid update channel role request.");
        }

        channelService.updateUserChannelRole(channelId, params);
        messagingTemplate.convertAndSend(CHANNEL_PREFIX + channelId, SendMessageDto.builder()
                .data(params)
                .type(MessageType.CHANNEL_USER_ROLE_CHANGE)
                .build());
        return new ResponseEntity<>(new ResponseDto(ResponseType.SUCCESS), HttpStatus.OK);
    }

    @ApiOperation(value = "채널 Permission 권한 수정")
    @PutMapping("/{channelId}/permission")
    public ResponseEntity<?> updateChannelPermissions(@PathVariable("channelId") String channelId,
                                                      @Valid @RequestBody RequestUpdateChannelPermissionsDto params) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        // 대상 채널이 활성화 중이고 채널의 OWNER가 요청했는 지 체크
        //  - 채널이 없거나 OWNER가 아닌 사용자가 잘못 요청한 경우 400 상태코드 반환
        if (channelService.isActiveChannelByOwnerUserIdAndChannelId(userId, channelId) == null)
            throw new BadRequestException("Invalid update channel permission request.");

        channelService.updateChannelPermissions(channelId, params);
        messagingTemplate.convertAndSend(CHANNEL_PREFIX + channelId, SendMessageDto.builder()
                .data(params)
                .type(MessageType.CHANNEL_PERMISSION_CHANGE)
                .build());
        return new ResponseEntity<>(new ResponseDto(ResponseType.SUCCESS), HttpStatus.OK);
    }


    @ApiOperation(value = "채널 검색- 채널명, 해시태그")
    @GetMapping("/search")
    public ResponseEntity<?> searchChannels(@RequestParam(value = "cursor", defaultValue = "1") int cursor,
                                            @RequestParam(value = "perPage", defaultValue = "10") int perPage,
                                            @RequestParam(value = "keyword", required = false) String keyword) {

        ResponseSearchChannelsDto response = channelService.getSearchChannels(cursor, perPage, keyword);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "채널 검색 - 해시태그")
    @GetMapping("/search-by-hashtag")
    public ResponseEntity<?> searchChannelsByHashtag(@RequestParam(value = "cursor", defaultValue = "1") int cursor,
                                                     @RequestParam(value = "perPage", defaultValue = "10") int perPage,
                                                     @RequestParam(value = "keyword", required = false) String keyword) {

        ResponseSearchChannelsDto response = channelService.getSearchChannelsByHashtag(cursor, perPage, keyword);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "채널 접속 유저 목록 조회")
    @GetMapping("/{channelId}/user")
    public ResponseEntity<?> getChannelUsers(@PathVariable("channelId") String channelId,
                                             @RequestParam(value = "cursor", defaultValue = "1") int cursor,
                                             @RequestParam(value = "perPage", defaultValue = "10") int perPage) {

        ResponseGetChannelUsersDto response = channelUserService.getChannelUsers(channelId, cursor, perPage);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "채널 접속 유저 검색")
    @GetMapping("/{channelId}/search-user")
    public ResponseEntity<?> searchChannelUsers(@PathVariable("channelId") String channelId,
                                                @RequestParam(value = "cursor", defaultValue = "1") int cursor,
                                                @RequestParam(value = "perPage", defaultValue = "10") int perPage,
                                                @RequestParam(value = "keyword", required = false) String keyword) {

        ResponseSearchChannelUsersDto response =  channelUserService.searchChannelUsers(channelId, cursor, perPage, keyword);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
