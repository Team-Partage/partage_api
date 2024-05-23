package com.egatrap.partage.controller;

import com.egatrap.partage.model.dto.*;
import com.egatrap.partage.service.PlaylistService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/playlist")
@PreAuthorize("hasRole('USER')")
public class PlaylistController {

    private final PlaylistService playlistService;
    private static final String VIDEO_ID_PATTERN = "^[a-zA-Z0-9_-]{11}$";

    @PostMapping(value = "", produces = "application/json")
    public ResponseEntity<?> addPlaylist(@Validated @RequestBody RequestAddPlaylistDto params) {
        String videoId = null;
        String url = params.getUrl();

//        비디오 아이디 추출
        if (url.startsWith("https://www.youtube.com/"))
            throw new IllegalArgumentException("Invalid URL : Not Youtube URL");

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
        if (videoId == null)
            throw new IllegalArgumentException("Invalid URL : Not Found Video ID");
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

    @GetMapping(value = "/{channelId}", produces = "application/json")
    public ResponseEntity<?> getPlaylist(
            @NotNull @PathVariable("channelId") Long channelId,
            @Min(1) @RequestParam(value = "page", defaultValue = "1") int page,
            @Min(1) @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        long totalPlaylist = playlistService.getTotalPlaylist(channelId);

        List<PlaylistDto> playlists = playlistService.getPlaylists(channelId, page, pageSize);

        return ResponseEntity.ok(ResponseGetPlaylistDto.builder()
                .playlists(playlists)
                .totalContents(totalPlaylist)
                .pageSize(pageSize)
                .page(page)
                .build());
    }

    @DeleteMapping(value = "/{playlistNo}", produces = "application/json")
    public ResponseEntity<?> deletePlaylist(
            @NotNull @PathVariable("playlistNo") Long playlistNo) {

        playlistService.deletePlaylist(playlistNo);

        return ResponseEntity.ok(ResponseDto.builder()
                .status(true)
                .message("Success to delete playlist, playlist no: " + playlistNo)
                .build());
    }

    @PostMapping(value = "/{playlistNo}/move", produces = "application/json")
    public ResponseEntity<?> movePlaylist(@NotNull @PathVariable("playlistNo") Long playlistNo,
                                          @Validated @RequestBody RequestMovePlaylistDto params) {

        playlistService.movePlaylist(playlistNo, params.getSequence());

        return ResponseEntity.ok(ResponseDto.builder()
                .status(true)
                .message("Success to move playlist, playlist no: " + playlistNo)
                .build());
    }
}
