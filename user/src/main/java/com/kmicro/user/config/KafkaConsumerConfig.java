package com.kmicro.user.config;

import com.kmicro.user.constants.KafkaConstants;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

        @Value("${spring.kafka.bootstrap-servers}")
        private String bootstrapServers;

        @Bean
        public ConsumerFactory<String, String> usersConsumerFactory() {
            Map<String, Object> props = new HashMap<>();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            props.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstants.USERS_GROUP_ID);
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            // Performance: increase fetch size for high throughput
            props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);

            return new DefaultKafkaConsumerFactory<>(props);
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, String> userKafkaListenerContainerFactory(
                DefaultErrorHandler errorHandler) { // Inject error handler
            ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(usersConsumerFactory());

            // Set manual ack mode
            factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
            factory.getContainerProperties().setAsyncAcks(true);

            // Ensure the container stops immediately on a fatal error
            // rather than trying to process the rest of the batch
            factory.getContainerProperties().setStopImmediate(true);

            // If you enable setBatchListener(), your listen method must accept a List<String> or List<ConsumerRecord>,
            //  or the app will throw a MethodArgumentResolutionException
//         factory.setBatchListener(true);
            // setAutoStartup()  is true by default
//        factory.setAutoStartup(true);

            // Increase concurrency to match partition count for parallel processing
            factory.setConcurrency(3);
            factory.setCommonErrorHandler(errorHandler);

            return factory;
        }

        @Bean
        public DefaultErrorHandler defaultErrorHandler(KafkaTemplate<String, String> template) {
            // 1. Define the Backoff (Interval: 5s, Max Attempts: 3)
            FixedBackOff fixedBackOff = new FixedBackOff(5000L, 3L);

            // 2. Define the Recoverer (Sends to DLT after max attempts)
            DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template);

            // 3. Create the Handler
            return new DefaultErrorHandler(recoverer, fixedBackOff);
        }

}
