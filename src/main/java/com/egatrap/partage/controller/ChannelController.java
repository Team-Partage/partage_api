package com.egatrap.partage.controller;

import com.egatrap.partage.constants.ResponseType;
import com.egatrap.partage.model.dto.ErrorMessageDto;
import com.egatrap.partage.model.dto.RequestCreateChannelDto;
import com.egatrap.partage.model.dto.ResponseDto;
import com.egatrap.partage.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        //  - 채널이 있다면 409 에러코드 반환
        if (channelService.isActiveChannelByUserNo(userNo))
        {
            return new ResponseEntity<>(ErrorMessageDto.builder()
                    .code(409)
                    .status("CONFLICT")
                    .message("The resource already exists")
                    .build(),
                    HttpStatus.CONFLICT);
        }

        // 채널 생성 및 채널 부가 정보 생성
        channelService.createChannel(params, userNo);

        // ToDo. 응답 데이터 정의 및 응답 수정 필요
        return new ResponseEntity<>(
                new ResponseDto(ResponseType.SUCCESS),
                HttpStatus.OK);
    }
}
