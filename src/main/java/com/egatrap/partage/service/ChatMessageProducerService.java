package com.egatrap.partage.service;

import com.egatrap.partage.model.dto.chat.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(ChatMessage message) {
        kafkaTemplate.send("chat-messages", message.getChannelId(), message);
    }
}
