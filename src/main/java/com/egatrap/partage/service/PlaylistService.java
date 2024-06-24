package com.egatrap.partage.service;

import com.egatrap.partage.exception.BadRequestException;
import com.egatrap.partage.exception.ConflictException;
import com.egatrap.partage.model.dto.PlaylistDto;
import com.egatrap.partage.model.entity.ChannelEntity;
import com.egatrap.partage.model.entity.PlaylistEntity;
import com.egatrap.partage.repository.ChannelRepository;
import com.egatrap.partage.repository.PlaylistRepository;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service("playlistService")
public class PlaylistService {

    @Value("${youtube.apikey}")
    private String youtubeApiKey;

    private final PlaylistRepository playlistRepository;
    private final ChannelRepository channelRepository;
    private final ModelMapper modelMapper;

    private static final String APPLICATION_NAME = "youtube-video-info";
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final Gson gson;

    @Transactional(rollbackFor = Exception.class, timeout = 10)
    public PlaylistDto addPlaylist(String channelId, String videoId) throws GeneralSecurityException, IOException {
        VideoListResponse response;

//        채널 플레이리스트 조회
        ChannelEntity channelEntity = channelRepository.findByChannelIdAndIsActive(channelId, true)
                .orElseThrow(() -> new BadRequestException("Channel not found. channelId=" + channelId));
        log.debug("channelEntity={}", channelEntity);

        // 채널이 가지고 있는 플레이리스트 개수 조회
        int playlistCount = playlistRepository.countByChannel_ChannelIdAndIsActive(channelId, true);

        // 플레이리스트 수가 50개 이상인 경우 예외 처리
        if (playlistCount >= 50) {
            throw new ConflictException("The number of playlists exceeds the limit. channelId=" + channelId);
        }

        // videoId에 해당하는 비디오 정보 조회 (YouTube Data API)
        Video video = getVideoById(videoId);

        // 플레이리스트 엔티티 생성
        PlaylistEntity playlistEntity = PlaylistEntity.builder()
                .channel(channelEntity)
                .sequence(playlistCount)
                .title(video.getSnippet().getTitle())
                .url(videoId) // @TODO: URL 생성 로직 수정
                .thumbnail(video.getSnippet().getThumbnails().getMedium().getUrl())
                .isActive(true)
                .build();
        log.debug("playlistEntity={}", playlistEntity);

        // 플레이리스트 저장
        playlistRepository.save(playlistEntity);

        return modelMapper.map(playlistEntity, PlaylistDto.class);
    }

    public long getTotalPlaylist(String channelId) {
        return playlistRepository.countByChannel_ChannelIdAndIsActive(channelId, true);
    }

    public List<PlaylistDto> getPlaylists(String channelId, int page, int pageSize) {

        Pageable pageable = Pageable.ofSize(pageSize).withPage(page - 1);

        // 채널이 가지고 있는 플레이리스트 조회
        // - 페이징 처리
        // - isActive가 true인 것만
        // - 조회 sequence 순으로 내림차순 정렬
        return playlistRepository.findByChannel_ChannelIdAndIsActiveOrderBySequence(channelId, true, pageable)
                .stream()
                .map(playlistEntity -> modelMapper.map(playlistEntity, PlaylistDto.class))
                .toList();
    }

    public List<PlaylistDto> getNonePaingPlaylists(String channelId)
    {
        return playlistRepository.findAllByChannel_ChannelIdAndIsActiveOrderBySequence(channelId, true)
                .stream()
                .map(playlistEntity -> modelMapper.map(playlistEntity, PlaylistDto.class))
                .toList();
    }


    @Transactional(rollbackFor = Exception.class, timeout = 10)
    public void deletePlaylist(Long playlistNo) {
        PlaylistEntity playlistEntity = playlistRepository.findById(playlistNo)
                .orElseThrow(() -> new BadRequestException("Playlist not found. playlistNo=" + playlistNo));

        if (!playlistEntity.getIsActive())
            throw new ConflictException("Playlist is already deleted. playlistNo=" + playlistNo);

        playlistEntity.setIsActive(false);
        playlistRepository.save(playlistEntity);

        // sequence 재정렬 처리 (삭제된 플레이리스트 이후의 sequence를 -1씩 수정)
        playlistRepository.findByChannel_ChannelIdAndIsActiveOrderBySequence(playlistEntity.getChannel().getChannelId(), true, Pageable.unpaged())
                .stream()
                .filter(entity -> entity.getSequence() > playlistEntity.getSequence())
                .forEach(entity -> {
                    entity.setSequence(entity.getSequence() - 1);
                    playlistRepository.save(entity);
                });
    }

    @Transactional(rollbackFor = Exception.class, timeout = 10)
    public void movePlaylist(Long playlistNo, int sequence) {
        // 플레이리스트 전체 조회 (채널별로 isActive가 true인 것만)
        List<PlaylistEntity> playlistEntities = playlistRepository.findByChannel_ChannelIdAndIsActiveOrderBySequence(
                playlistRepository.findById(playlistNo)
                        .orElseThrow(() -> new BadRequestException("Playlist not found. playlistNo=" + playlistNo))
                        .getChannel().getChannelId(), true, Pageable.unpaged());

        // sequence가 플레이리스트 개수보다 큰 경우 마지막으로 이동
        int location = sequence >= playlistEntities.size() ? playlistEntities.size() - 1 : sequence;

        // 플레이리스트 이동
        playlistEntities.stream()
                .filter(entity -> entity.getPlaylistNo().equals(playlistNo))
                .findFirst()
                .ifPresent(entity -> {
                    playlistEntities.remove(entity);
                    playlistEntities.add(location, entity);
                });

        // 플레이리스트 sequence 재정렬 후 저장
        for (int i = 0; i < playlistEntities.size(); i++) {
            PlaylistEntity entity = playlistEntities.get(i);
            entity.setSequence(i);
            playlistRepository.save(entity);
        }
    }

    private Video getVideoById(String id) {
        try {
            YouTube client = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, null)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            // videoId에 해당하는 비디오 정보 조회
            YouTube.Videos.List request = client.videos()
                    .list("snippet,contentDetails,statistics")
                    .setId(id)
                    .setKey(youtubeApiKey);
            VideoListResponse response = request.execute();

            // videoId에 해당하는 비디오가 없는 경우 예외 처리
            if (response.getItems().isEmpty())
                throw new BadRequestException("YouTube video not found. videoId=" + id);

            return response.getItems().get(0);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Failed to youtube connection error", e);
        }
    }
}
