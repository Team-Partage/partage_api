package com.egatrap.partage.service;

import com.egatrap.partage.model.dto.chat.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@RequiredArgsConstructor
public class ChatMessageConsumerService {

    private final ConcurrentLinkedQueue<ChatMessage> messageQueue = new ConcurrentLinkedQueue<>();

    @KafkaListener(topics = "chat-messages", groupId = "chat-backup-group")
    public void listen(ConsumerRecord<String, ChatMessage> record) {
        // System.out.println("Received message: " + record.value());
        messageQueue.add(record.value());
    }

    public List<ChatMessage> getMessages() {
        List<ChatMessage> messages = new ArrayList<>();
        while (!messageQueue.isEmpty()) {
            messages.add(messageQueue.poll());
        }
        return messages;
    }
}
