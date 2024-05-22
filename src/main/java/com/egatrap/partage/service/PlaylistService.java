package com.egatrap.partage.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Slf4j
@RequiredArgsConstructor
@Service("playlistService")
public class PlaylistService {

    @Value("${youtube.apikey}")
    private String youtubeApiKey;

    private static final String APPLICATION_NAME = "youtube-video-info";
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final Gson gson;

    @Transactional(rollbackFor = Exception.class, timeout = 10)
    public boolean addPlaylist(Long channelNo, String videoId) throws GeneralSecurityException, IOException {

        YouTube client = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build();

        YouTube.Videos.List request = client.videos()
                .list("snippet,contentDetails,statistics")
                .setId(videoId)
                .setKey(youtubeApiKey);

        VideoListResponse response = request.execute();
        if (response.getItems().isEmpty()) {
            return false;
        }

        log.debug("[response]=[{}]", response.getItems().get(0));

        return true;
    }
}
