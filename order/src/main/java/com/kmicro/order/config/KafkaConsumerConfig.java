package com.kmicro.order.config;

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
    public ConsumerFactory<String, String> orderConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-service-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Performance: increase fetch size for high throughput
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> orderKafkaListenerContainerFactory(
            DefaultErrorHandler errorHandler) { // Inject error handler
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderConsumerFactory());

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

/*    @Bean
    public ConsumerFactory<String, String> orderConsumerFactory() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "dev" + "_" + "order-group");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
//        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, KafkaConstants.ENABLE_AUTO_COMMIT_CONFIG);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, Integer.parseInt(this.environment.getProperty("kafka.consumers.maxPollIntervalMsConfigExcelUpload")));
//        properties.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, Integer.parseInt(this.environment.getProperty("kafka.consumers.heartbeatIntervalMsConfigExcelUpload")));
//        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, Integer.parseInt(this.environment.getProperty("kafka.consumers.maxPollRecordsConfigExcelUpload")));
//        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, Integer.parseInt(this.environment.getProperty("kafka.consumers.sessionTimeoutMsConfigExcelUpload")));
        return new DefaultKafkaConsumerFactory<>(properties);
    }*/

    /*@Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> orderKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> concurrentKafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        concurrentKafkaListenerContainerFactory.setConsumerFactory(orderConsumerFactory());
//        concurrentKafkaListenerContainerFactory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
//        concurrentKafkaListenerContainerFactory.setBatchListener(true);
//        concurrentKafkaListenerContainerFactory.setAutoStartup(true);
        concurrentKafkaListenerContainerFactory.setConcurrency(1);
        return concurrentKafkaListenerContainerFactory;
    }*/
}
