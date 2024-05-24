package com.egatrap.partage.controller;

import com.egatrap.partage.constants.ResponseType;
import com.egatrap.partage.model.dto.*;
import com.egatrap.partage.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
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
        Long userNo = Long.parseLong(authentication.getName());

        // 사용자가 생성한 활성화 채널이 있는지 체크
        //  - 채널이 있다면 409 상태코드 반환
        if (channelService.isExistsActiveChannelByUserNo(userNo)) {
            return new ResponseEntity<>(ErrorMessageDto.builder()
                    .code(409)
                    .status("CONFLICT")
                    .message("The resource already exists")
                    .build(),
                    HttpStatus.CONFLICT);
        }

        // 채널 생성 및 채널 권한 설정 정보 생성
        ResponseCreateChannelDto responseCreateChannelDto = channelService.createChannel(params, userNo);
        return new ResponseEntity<>(
                responseCreateChannelDto,
                HttpStatus.OK);
    }

    /**
     * 채널 수정
     */
    @PutMapping("/{channelNo}")
    public ResponseEntity<?> updateChannel(@PathVariable("channelNo") Long channelNo,
                                           @RequestBody RequestUpdateChannelDto params) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userNo = Long.parseLong(authentication.getName());

        // 대상 채널이 활성화 중이고 채널의 OWNER가 요청했는 지 체크
        //  - 채널이 없거나 OWNER가 아닌 사용자가 잘못 요청한 경우 400 상태코드 반환
        if (channelService.isActiveChannelByOwnerUserNoAndChannelNo(userNo, channelNo) == null) {
            return new ResponseEntity<>(
                    ErrorMessageDto.builder()
                            .code(400)
                            .status("BAD REQUEST")
                            .message("Invalid update channel request")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }

        // 채널 생성 및 채널 권한 설정 정보 생성
        ResponseUpdateChannelDto responseUpdateChannelDto = channelService.updateChannel(channelNo, params);
        return new ResponseEntity<>(
                responseUpdateChannelDto,
                HttpStatus.OK);
    }

    /**
     * 채널 삭제
     */
    @DeleteMapping("/{channelNo}")
    public ResponseEntity<?> deleteChannel(@PathVariable("channelNo") Long channelNo) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userNo = Long.parseLong(authentication.getName());

        // 대상 채널이 활성화 중이고 채널의 OWNER가 요청했는 지 체크
        //  - 채널이 없거나 OWNER가 아닌 사용자가 잘못 요청한 경우 상태코드 반환
        if (channelService.isActiveChannelByOwnerUserNoAndChannelNo(userNo, channelNo) == null) {
            return new ResponseEntity<>(
                    ErrorMessageDto.builder()
                            .code(400)
                            .status("BAD REQUEST")
                            .message("Invalid delete channel request")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }

        channelService.deleteChannel(channelNo);
        return new ResponseEntity<>(
                new ResponseDto(ResponseType.SUCCESS),
                HttpStatus.OK);
    }

    /**
     * 채널 상세 정보 조회
     */
    @GetMapping("/{channelNo}")
    public ResponseEntity<?> getChannelDetailInfo(@PathVariable("channelNo") Long channelNo) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userNo = Long.parseLong(authentication.getName());

        // 대상 채널이 활성화 중이고 채널 사용자가 요청했는지 체크
        //  - 채널이 비활성화이거나 채널 사용자가 아닌 경우 400 상태코드 반환
        if (channelService.isActiveChannelByUserNoAndChannelNo(userNo, channelNo) == null) {
            return new ResponseEntity<>(
                    ErrorMessageDto.builder()
                            .code(400)
                            .status("BAD REQUEST")
                            .message("Invalid get channel info request")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }

        ResponseGetChannelDetailInfoDto responseGetChannelDetailInfoDto = channelService.getChannelDetailInfo(userNo, channelNo);
        return new ResponseEntity<>(
                responseGetChannelDetailInfoDto,
                HttpStatus.OK);
    }
}
