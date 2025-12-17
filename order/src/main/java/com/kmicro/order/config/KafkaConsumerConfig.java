package com.kmicro.order.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

//    @Value("${kafka.bootstrap-servers}")
//    private String BOOTSTRAP_SERVERS_CONFIG;

    @Bean
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
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> orderKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> concurrentKafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        concurrentKafkaListenerContainerFactory.setConsumerFactory(orderConsumerFactory());
//        concurrentKafkaListenerContainerFactory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
//        concurrentKafkaListenerContainerFactory.setBatchListener(true);
//        concurrentKafkaListenerContainerFactory.setAutoStartup(true);
        concurrentKafkaListenerContainerFactory.setConcurrency(1);
        return concurrentKafkaListenerContainerFactory;
    }
}
