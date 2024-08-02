package com.egatrap.partage.schedule;

import com.egatrap.partage.model.dto.chat.ChatMessage;
import com.egatrap.partage.model.entity.ChannelEntity;
import com.egatrap.partage.model.entity.ChattingEntity;
import com.egatrap.partage.model.entity.UserEntity;
import com.egatrap.partage.repository.ChannelRepository;
import com.egatrap.partage.repository.ChattingRepository;
import com.egatrap.partage.repository.UserRepository;
import com.egatrap.partage.service.ChatMessageConsumerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChattingBackupScheduler {

    private final ChatMessageConsumerService consumerService;
    private final ChattingRepository chattingRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    @Scheduled(fixedRate = 60000)   // 1분마다 실행
    public void chattingBackup() {
        List<ChatMessage> messageDtoList = consumerService.getMessages();
        List<ChattingEntity> messageEntityList = new ArrayList<>();

        for (ChatMessage messageDto : messageDtoList)
            messageEntityList.add(convertToEntity(messageDto));

        if (!messageEntityList.isEmpty())
            chattingRepository.saveAll(messageEntityList);
    }

    private ChattingEntity convertToEntity(ChatMessage messageDto) {
        ChannelEntity channel = channelRepository.findById(messageDto.getChannelId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid channel ID"));
        UserEntity user = userRepository.findById(messageDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        return ChattingEntity.builder()
                .channel(channel)
                .user(user)
                .message(messageDto.getContent())
                .createAt(messageDto.getCreateAt())
                .isActive(true)
                .build();
    }
}
