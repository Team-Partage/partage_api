package com.egatrap.partage.schedule;

import com.egatrap.partage.constants.MessageType;
import com.egatrap.partage.model.dto.ChannelSessionDto;
import com.egatrap.partage.model.dto.chat.SendMessageDto;
import com.egatrap.partage.service.ChannelService;
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


    @Scheduled(fixedRate = 10000)
    public void sendPeriodicMessages() {
        for (ChannelSessionDto channel : channelUserService.getChannels()) {
//            log.info("Send Message : {}", channel.getId());
            messagingTemplate.convertAndSend(CHANNEL_PREFIX + channel.getId(), SendMessageDto.builder()
                    .data(channelUserService.countUserByChannel(channel.getId()))
                    .type(MessageType.CHANNEL_VIEWER)
                    .build());
        }
    }

    @Scheduled(fixedRate = 1000)
    public void sendCurrentPlayTime() {
        List<ChannelSessionDto> channels = channelUserService.getChannels();
        for (ChannelSessionDto channel : channels) {
            messagingTemplate.convertAndSend(CHANNEL_PREFIX + channel.getId(), SendMessageDto.builder()
                    .data(channel.getCurrentPlayTime())
                    .type(MessageType.VIDEO_CURRENT)
                    .build());
        }
    }

    @Scheduled(fixedRate = 10000)
    public void ChannelMonitoring() {
        long channels = channelUserService.countChannel();
        long users = channelUserService.countUser();
        log.info(">> Session Check - Channel [{}] : User [{}]", channels, users);
    }

    @Scheduled(fixedRate = 30000)
    public void syncChannelViewerCount() {
        long updatedChannel = channelService.syncChannelViewerCount();
        log.info(">> Sync Channel Viewer Count : {}", updatedChannel);
    }
}
