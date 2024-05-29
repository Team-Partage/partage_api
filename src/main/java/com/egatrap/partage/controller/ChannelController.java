package com.egatrap.partage.controller;

import com.egatrap.partage.constants.ChannelRoleType;
import com.egatrap.partage.constants.ResponseType;
import com.egatrap.partage.exception.BadRequestException;
import com.egatrap.partage.exception.ConflictException;
import com.egatrap.partage.model.dto.*;
import com.egatrap.partage.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/channel")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    /**
     * 채널 생성
     */
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
        return new ResponseEntity<>(
                responseCreateChannelDto,
                HttpStatus.OK);
    }

    /**
     * 채널 수정
     */
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
        return new ResponseEntity<>(
                responseUpdateChannelDto,
                HttpStatus.OK);
    }

    /**
     * 채널 삭제
     */
    @DeleteMapping("/{channelId}")
    public ResponseEntity<?> deleteChannel(@PathVariable("channelId") String channelId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        // 대상 채널이 활성화 중이고 채널의 OWNER가 요청했는 지 체크
        //  - 채널이 없거나 OWNER가 아닌 사용자가 잘못 요청한 경우 400 상태코드 반환
        if (channelService.isActiveChannelByOwnerUserIdAndChannelId(userId, channelId) == null)
            throw new BadRequestException("Invalid delete channel request.");

        channelService.deleteChannel(channelId);
        return new ResponseEntity<>(
                new ResponseDto(ResponseType.SUCCESS),
                HttpStatus.OK);
    }

    /**
     * 채널 상세 정보 조회
     */
    @GetMapping("/{channelId}")
    public ResponseEntity<?> getChannelDetailInfo(@PathVariable("channelId") String channelId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        // 대상 채널이 활성화 중이고 채널 사용자가 요청했는지 체크
        //  - 채널이 비활성화이거나 채널 사용자가 아닌 경우 400 상태코드 반환
        if (channelService.isActiveChannelByUserIdAndChannelId(userId, channelId) == null)
            throw new BadRequestException("Invalid get channel info request.");

        ResponseGetChannelDetailInfoDto responseGetChannelDetailInfoDto = channelService.getChannelDetailInfo(userId, channelId);
        return new ResponseEntity<>(
                responseGetChannelDetailInfoDto,
                HttpStatus.OK);
    }

    /**
     * 채널 권한 수정
     */
    @PatchMapping("/role/{channelId}")
    public ResponseEntity<?> updateUserChannelRole(@PathVariable("channelId") String channelId,
                                                   @Valid @RequestBody RequestUpdateUserChannelRoleDto params) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        // 1. 대상 채널이 활성화 중이고 채널의 OWNER가 요청했는 지 체크
        //  - 채널이 없거나 OWNER가 아닌 사용자가 잘못 요청한 경우 400 상태코드 반환
        // 2. onwer 권한 변경 요청 시 400 상태코드 반환
        // 3. 일반 사용자를 owner로 요청한 경우 400 상태코드 반환
        // 4. 대상 채널이 활성화 중이고 채널 사용자가 타겟인지 체크
        //   - 사용자가 대상 채널에 비활성화 중이거나 대상 채널에 존재 하지 않는 경우 400 상태코드 반환
        if (channelService.isActiveChannelByOwnerUserIdAndChannelId(userId, channelId) == null ||
            channelService.isActiveChannelByUserIdAndChannelId(params.getUserId(), channelId) == null ||
            params.getUserId().equals(userId) || params.getRoleId().equals(ChannelRoleType.ROLE_OWNER.getROLE_ID()) ) {

            throw new BadRequestException("Invalid userChannelRole update request.");
        }

        channelService.updateUserChannelRole(channelId, params);
        return new ResponseEntity<>(
                new ResponseDto(ResponseType.SUCCESS),
                HttpStatus.OK);
    }
}
