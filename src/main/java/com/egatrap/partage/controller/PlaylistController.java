package com.egatrap.partage.controller;

import com.egatrap.partage.model.dto.*;
import com.egatrap.partage.service.PlaylistService;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperation(value = "플레이리스트 추가")
    @PostMapping(value = "", produces = "application/json")
    public ResponseEntity<?> addPlaylist(@Validated @RequestBody RequestAddPlaylistDto params) throws Exception {
        String videoId = null;
        String url = params.getUrl();

//        비디오 아이디 추출
        if (!url.startsWith("https://www.youtube.com/"))
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

        // 플레이리스트 추가
        playlistService.addPlaylist(params.getChannelId(), videoId);


        return ResponseEntity.ok(ResponseDto.builder()
                .status(true)
                .message("Success to add playlist, video id: " + videoId)
                .build());
    }

    @ApiOperation(value = "플레이리스트 조회")
    @GetMapping(value = "/{channelId}", produces = "application/json")
    public ResponseEntity<?> getPlaylist(
            @NotNull @PathVariable("channelId") String channelId,
            @Min(1) @RequestParam(value = "page", defaultValue = "1") int page,
            @Min(1) @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) throws Exception {

        long totalPlaylist = playlistService.getTotalPlaylist(channelId);

        List<PlaylistDto> playlists = playlistService.getPlaylists(channelId, page, pageSize);

        return ResponseEntity.ok(ResponseGetPlaylistDto.builder()
                .playlists(playlists)
                .totalContents(totalPlaylist)
                .pageSize(pageSize)
                .page(page)
                .build());
    }

    @ApiOperation(value = "플레이리스트 삭제")
    @DeleteMapping(value = "/{playlistNo}", produces = "application/json")
    public ResponseEntity<?> deletePlaylist(
            @NotNull @PathVariable("playlistNo") Long playlistNo) throws Exception {

        playlistService.deletePlaylist(playlistNo);

        return ResponseEntity.ok(ResponseDto.builder()
                .status(true)
                .message("Success to delete playlist, playlist no: " + playlistNo)
                .build());
    }

    @ApiOperation(value = "플레이리스트 이동")
    @PutMapping(value = "/move", produces = "application/json")
    public ResponseEntity<?> movePlaylist(@Validated @RequestBody RequestMovePlaylistDto params) throws Exception {

        playlistService.movePlaylist(params.getPlaylistNo(), params.getSequence());

        return ResponseEntity.ok(ResponseDto.builder()
                .status(true)
                .message("Success to move playlist, playlist no: " + params.getPlaylistNo())
                .build());
    }
}
