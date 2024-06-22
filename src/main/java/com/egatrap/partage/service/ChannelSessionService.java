package com.egatrap.partage.service;

import com.egatrap.partage.model.dto.ChannelSessionDto;
import com.egatrap.partage.model.entity.ChannelSessionEntity;
import com.egatrap.partage.repository.ChannelSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@RequiredArgsConstructor
@Service("channelSessionService")
public class ChannelSessionService {

    private final ChannelSessionRepository channelSessionRepository;

    public void updatePlayStatus(String channelId, boolean isPlaying) {
        ChannelSessionEntity channelSession = channelSessionRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found. channelId: " + channelId));

        channelSession.setPlaying(isPlaying);
        channelSession.setUpdateTime(LocalDateTime.now());

        channelSessionRepository.save(channelSession);
    }

    public int updatePlayTime(String channelId, int playTime) {
        ChannelSessionEntity channelSession = channelSessionRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found. channelId: " + channelId));

        channelSession.setPlayTime(playTime);
        channelSession.setUpdateTime(LocalDateTime.now());

        channelSessionRepository.save(channelSession);

        return getPlayTime(playTime, channelSession.getUpdateTime());
    }

    public int getPlayTime(String channelId) {
        ChannelSessionEntity channelSession = channelSessionRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found. channelId: " + channelId));
        return getPlayTime(channelSession.getPlayTime(), channelSession.getUpdateTime());
    }

    public int getPlayTime(ChannelSessionDto channel) {
        return getPlayTime(channel.getPlayTime(), channel.getUpdateTime());
    }

    private int getPlayTime(int playTime, LocalDateTime updateTime) {
        // 업데이트된 시간과 현재 시간의 차이를 초 단위로 구한다.
        long diff = ChronoUnit.SECONDS.between(updateTime, LocalDateTime.now());

        // 현재 플레이 타임에 차이를 더한다.
        return playTime + (int) diff;
    }


}
