  package com.campusnest.campusnest_platform.services;

  import com.campusnest.campusnest_platform.events.MessageEvent;
  import lombok.extern.slf4j.Slf4j;
  import org.springframework.kafka.annotation.KafkaListener;
  import org.springframework.stereotype.Service;

  @Service
  @Slf4j
  public class MessageAnalyticsConsumer {

      @KafkaListener(
          topics = "${kafka.topic.chat-messages}", 
          groupId = "analytics-service",
          containerFactory = "kafkaListenerContainerFactory"
      )
      public void handleMessageForAnalytics(MessageEvent event) {
          try {
              log.info("Processing message event for analytics - MessageId: {}, ConversationId: {}, HousingListingId: {}",
                  event.getMessageId(),
                  event.getConversationId(),
                  event.getHousingListingId());

              // TODO: Implement analytics tracking
              // Examples:
              // - Track message volume by hour/day
              // - Track response times between users
              // - Track popular housing listings with most inquiries
              // - Track user engagement metrics

              // Future implementation:
              // analyticsService.recordMessage(event);
              // metricsService.incrementMessageCounter(event.getConversationId());

          } catch (Exception e) {
              log.error("Error processing message for analytics: {}", e.getMessage(), e);
          }
      }
  }

