package com.egatrap.partage.schedule;

import com.egatrap.partage.constants.MessageType;
import com.egatrap.partage.model.dto.ChannelSessionDto;
import com.egatrap.partage.model.dto.chat.SendMessageDto;
import com.egatrap.partage.service.ChannelService;
import com.egatrap.partage.service.ChannelSessionService;
import com.egatrap.partage.service.ChannelUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.egatrap.partage.controller.StompController.CHANNEL_PREFIX;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageScheduler {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChannelUserService channelUserService;
    private final ChannelService channelService;
    private final ChannelSessionService channelSessionService;


    /**
     * 채널의 시청자 수를 실시간으로 푸시
     * 30초마다 실행
     */
    @Scheduled(fixedRate = 30000)
    public void sendPeriodicMessages() {
        for (ChannelSessionDto channel : channelUserService.getChannels()) {
//            log.info("Send Message : {}", channel.getId());
            messagingTemplate.convertAndSend(CHANNEL_PREFIX + channel.getId(), SendMessageDto.builder()
                    .data(channelUserService.countUserByChannel(channel.getId()))
                    .type(MessageType.CHANNEL_VIEWER)
                    .build());
        }
    }

    /**
     * 채널의 현재 재생 시간을 실시간으로 푸시
     * 1초마다 실행
     */
    @Scheduled(fixedRate = 1000)
    public void sendCurrentPlayTime() {
        List<ChannelSessionDto> channels = channelUserService.getChannels();
        for (ChannelSessionDto channel : channels) {
            if (channel.isPlaying()) {
                int currentPlayTime = channelSessionService.getPlayTime(channel);
                messagingTemplate.convertAndSend(CHANNEL_PREFIX + channel.getId(), SendMessageDto.builder()
                        .data(currentPlayTime)
                        .type(MessageType.VIDEO_TIME)
                        .build());
            }
        }
    }

    /**
     * 채널의 세션 수를 모니터링
     * 1분마다 실행
     */
    @Scheduled(fixedRate = 60000)
    public void ChannelMonitoring() {
        long channels = channelUserService.countChannel();
        long users = channelUserService.countUser();
        log.info(">> Session Check - Channel [{}] : User [{}]", channels, users);
    }

    /**
     * 채널의 시청자 수를 실시간으로 동기화
     * 10분마다 실행
     */
    @Scheduled(fixedRate = 600000)
    public void syncChannelViewerCount() {
        long updatedChannel = channelUserService.syncChannelViewerCount();
        log.info(">> Sync Channel Viewer Count : {}", updatedChannel);
    }

    /**
     * 비활성화된 채널 제거
     * 5분마다 실행
     */
//    @Scheduled(fixedRate = 300000)
//    public void removeInactiveChannel() {
//        long removedChannel = channelUserService.removeNoneUserChannel();
//        log.info(">> Remove Inactive Channel : {}", removedChannel);
//    }

}
