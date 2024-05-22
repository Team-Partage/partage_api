package com.egatrap.partage.controller;

import com.egatrap.partage.model.dto.ErrorMessageDto;
import com.egatrap.partage.model.dto.RequestAddPlaylistDto;
import com.egatrap.partage.model.dto.ResponseDto;
import com.egatrap.partage.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/playlist")
public class PlaylistController {

    private final PlaylistService playlistService;
    private static final String VIDEO_ID_PATTERN = "^[a-zA-Z0-9_-]{11}$";

    @PostMapping(value = "", produces = "application/json")
    public ResponseEntity<?> addPlaylist(@Validated @RequestBody RequestAddPlaylistDto params) {

//        비디오 아이디 추출
        String url = params.getUrl();
        String videoId = null;

        // URL에서 비디오 아이디 추출 (유튜브)
        Pattern pattern = Pattern.compile(VIDEO_ID_PATTERN);
        String[] parts = url.split("[/?=&]+");
        for (String part : parts) {
            Matcher matcher = pattern.matcher(part);
            if (matcher.matches()) {
                videoId = matcher.group();
                break;
            }
        }

        // 비디오 아이디가 없으면 URL이 잘못된 것으로 판단 후 에러 메시지 반환
        if (videoId == null) {
            return ResponseEntity.badRequest().body(new ErrorMessageDto(
                    HttpStatus.BAD_REQUEST,
                    "Invalid URL"));
        }
        log.debug("[videoId]=[{}]", videoId);

        try {
            playlistService.addPlaylist(params.getChannelNo(), videoId);
        } catch (GeneralSecurityException | IOException e) {
            log.error("Failed to youtube connection error", e);
            return ResponseEntity.badRequest().body(new ErrorMessageDto(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to youtube connection error"));
        }


        return ResponseEntity.ok(ResponseDto.builder()
                .status(true)
                .message("Success to add playlist, video id: " + videoId)
                .build());
    }

}
