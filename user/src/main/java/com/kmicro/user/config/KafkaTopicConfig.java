package com.kmicro.user.config;

import com.kmicro.user.constants.KafkaConstants;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public NewTopic userEventTopic() {
        // Industry practice: Explicitly define partitions and replication factor
        return TopicBuilder.name(KafkaConstants.USERS_TOPIC)
                .partitions(3)
                .replicas(1) // Set to 3 in production
                .compact()   // If you want to keep the latest state per key
                .build();
    }

}
