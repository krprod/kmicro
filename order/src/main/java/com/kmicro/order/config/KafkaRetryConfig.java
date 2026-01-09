package com.kmicro.order.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.RetryTopicConfiguration;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationBuilder;

@Configuration
public class KafkaRetryConfig {

    @Bean
    public RetryTopicConfiguration retryConfiguration(KafkaTemplate<String, String> template) {
        return RetryTopicConfigurationBuilder
                .newInstance()
                .maxAttempts(4) // 1 original + 3 retries
                .fixedBackOff(5000) // 5 seconds between each attempt
                .includeTopic("t-order-placed") // Only apply to this topic
                // Only retry on casting or network errors, not logic errors
                .retryOn(JsonProcessingException.class)
                .dltHandlerMethod("orderDltHandler", "processDlt")
                .create(template);
    }

}//EC
