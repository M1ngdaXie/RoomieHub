package com.campusnest.campusnest_platform.services;

import com.campusnest.campusnest_platform.events.MessageEvent;
import com.campusnest.campusnest_platform.events.UserPresenceEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class MessageEventProducer {
    @Autowired
    private KafkaTemplate<String, MessageEvent> messageKafkaTemplate;

    @Autowired
    private KafkaTemplate<String, UserPresenceEvent> presenceKafkaTemplate;

    @Value("${kafka.topic.chat-messages}")
    private String chatMessagesTopic;

    @Value("${kafka.topic.user-presence}")
    private String userPresenceTopic;

    public void publishMessageEvent(MessageEvent event){
        try{
            String key = event.getConversationId().toString();
            CompletableFuture<SendResult<String, MessageEvent>> future =
                    messageKafkaTemplate.send(chatMessagesTopic, key, event);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Published message event to Kafka - MessageId: {}, ConversationId: {}, Offset: {}",
                            event.getMessageId(),
                            event.getConversationId(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish message event to Kafka - MessageId: {}, Error: {}",
                            event.getMessageId(),
                            ex.getMessage());
                }
            });
        }catch (Exception e){
            log.error("Failed to publish message event to Kafka: {}", e.getMessage(), e);
        }
    }
    public void publishPresenceEvent(UserPresenceEvent event) {
        try {
            String key = event.getUserId().toString();

            CompletableFuture<SendResult<String, UserPresenceEvent>> future =
                    presenceKafkaTemplate.send(userPresenceTopic, key, event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.debug("Published presence event to Kafka - UserId: {}, Status: {}",
                            event.getUserId(),
                            event.getStatus());
                } else {
                    log.error("Failed to publish presence event to Kafka: {}", ex.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("Error publishing presence event to Kafka: {}", e.getMessage(), e);
        }
    }
}

