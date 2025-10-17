package com.campusnest.campusnest_platform.services;

import com.campusnest.campusnest_platform.events.MessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PushNotificationConsumer {

    @Autowired(required = false)
    private UserPresenceService presenceService;

    @KafkaListener(
            topics = "${kafka.topic.chat-messages}",
            groupId = "push-notification-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleMessageForPushNotification(MessageEvent event) {
        try {
            log.info("Processing message event for push notification - MessageId: {}, RecipientId: {}",
                    event.getMessageId(),
                    event.getRecipientId());

            // Check if recipient is online (if presenceService is available)
            boolean isOnline = presenceService != null &&
                    presenceService.isUserOnline(event.getRecipientId());

            if (!isOnline) {
                // TODO: Send actual push notification here
                // For now, just log the action
                log.info("User {} is OFFLINE - Would send push notification: '{}'",
                        maskEmail(event.getRecipientEmail()),
                        truncateContent(event.getContent()));

                // Future implementation:
                // pushNotificationService.sendNotification(
                //     event.getRecipientId(),
                //     "New message from " + event.getSenderName(),
                //     event.getContent()
                // );
            } else {
                log.debug("User {} is ONLINE - Skipping push notification (delivered via WebSocket)",
                        maskEmail(event.getRecipientEmail()));
            }

        } catch (Exception e) {
            log.error("Error processing message for push notification: {}", e.getMessage(), e);
        }
    }

    private String maskEmail(String email) {
        if (email == null) return "null";
        int atIndex = email.indexOf("@");
        return atIndex > 0 ? email.substring(0, 1) + "***" + email.substring(atIndex) : email;
    }

    private String truncateContent(String content) {
        if (content == null) return "";
        return content.length() > 50 ? content.substring(0, 50) + "..." : content;
    }
}
