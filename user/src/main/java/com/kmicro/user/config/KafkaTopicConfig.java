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

//    @Bean
//    public KafkaAdmin kafkaAdmin() {
//        Map<String, Object> configs = new HashMap<>();
//        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        KafkaAdmin admin = new KafkaAdmin(configs);
//        // This is the magic line for startup resilience
//        admin.setFatalIfBrokerNotAvailable(false);
//        return admin;
//    }

//    @Value("${kafka.bootstrap-servers}")
//    private String bootstrapServers;

//    @Bean
//    public KafkaAdmin kafkaAdmin() {
//        Map<String, Object> configs = new HashMap<>();
//        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        return new KafkaAdmin(configs);
//    }

//    @Bean
//    public NewTopic getPaymentTopic() {
//        return new NewTopic("payment-events",10, (short) 1);
//    }

}
