package com.campusnest.campusnest_platform.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Value("${kafka.topic.chat-messages}")
    private String chatMessagesTopic;

    @Value("${kafka.topic.user-presence}")
    private String userPresenceTopic;

    @Value("${kafka.topic.message-status}")
    private String messageStatusTopic;

    @Bean
    public NewTopic chatMessagesTopic(){
        return TopicBuilder.name(chatMessagesTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic userPresenceTopic() {
        return TopicBuilder.name(userPresenceTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic messageStatusTopic() {
        return TopicBuilder.name(messageStatusTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
